import {Component, Input} from '@angular/core';
import {AccountSummary, AccountTypeUtil, Book, BookkeepingService} from "../../../bookkeeping.service";
import {MatOptgroup, MatOption, provideNativeDateAdapter} from "@angular/material/core";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput, MatInputModule} from "@angular/material/input";
import {
  MatDatepicker,
  MatDatepickerInput,
  MatDatepickerModule,
  MatDatepickerToggle
} from "@angular/material/datepicker";
import {MatDivider} from "@angular/material/divider";
import {MatSelect} from "@angular/material/select";
import {MatButton, MatFabButton, MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import Big from "big.js";

@Component({
  selector: 'app-transaction-creation',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatInputModule,
    MatFormField,
    MatDatepicker,
    MatDatepickerToggle,
    MatDatepickerInput,
    MatDatepickerModule,
    MatDivider,
    MatSelect,
    MatOption,
    MatOptgroup,
    MatButton,
    MatIcon,
    MatFabButton,
    MatMiniFabButton,
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './transaction-creation.component.html',
  styleUrl: './transaction-creation.component.scss'
})
export class TransactionCreationComponent {

  @Input() book!: Book

  form = new FormGroup({
    date: new FormControl(new Date()),
    text: new FormControl(''),
    tag: new FormControl(''),
    description: new FormControl('')
  })

  credits: AccountRow[] = [this.createAccountRow()]
  debits: AccountRow[] = [this.createAccountRow(true)]

  accountsValid = false

  constructor(private service: BookkeepingService) {
  }

  private createAccountRow(disabled: Boolean = false): AccountRow {
    let row = {
      account: new FormControl(null),
      amount: new FormControl("0", Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/))
    }
    if (disabled) {
      row.amount.disable()
    }
    return row
  }

  addCreditRow() {
    this.credits.push(this.createAccountRow())
    this.updateRowStates()
  }

  removeCreditRow(index: number) {
    this.credits.splice(index, 1)
    this.onAmountChanged()
    this.updateRowStates()
  }

  addDebitRow() {
    this.debits.push(this.createAccountRow())
    this.updateRowStates()
  }

  removeDebitRow(index: number) {
    this.debits.splice(index, 1)
    this.onAmountChanged()
    this.updateRowStates()
  }

  onAmountChanged() {
    const creditSum = this.credits
      .map(row => new Big(row.amount.value || 0))
      .reduce((a, b) => a.plus(b))
    const debitSum = this.debits.filter((_, index) => index > 0)
      .map(row => new Big(row.amount.value || 0))
      .reduce((a, b) => a.plus(b), Big(0))
    this.debits[0].amount.setValue(creditSum.minus(debitSum).toFixed(2))
    this.updateRowStates()
  }

  resetForm() {
    this.form.reset()
    this.credits = [this.createAccountRow()]
    this.debits = [this.createAccountRow(true)]
    this.updateRowStates()
  }

  updateRowStates() {
    this.accountsValid = this.validateRows()
  }

  validateRows(): boolean {
    let result = true
    // single-row validity
    for (let i = 0; i < this.credits.length; i++) {
      let credit = this.credits[i]
      if (!credit.amount.valid || !credit.account.valid) {
        credit.error = 'Invalid Inputs'
        result = false
      } else {
        credit.error = undefined
      }
    }
    for (let i = 0; i < this.debits.length; i++) {
      let debit = this.debits[i]
      if (i == 0) {
        if (!debit.account.valid) {
          debit.error = 'Invalid Inputs'
          result = false
        } else {
          debit.error = undefined
        }
      } else {
        if (!debit.amount.valid || !debit.account.valid) {
          debit.error = 'Invalid Inputs'
          result = false
        } else {
          debit.error = undefined
        }
      }
    }
    // validate all unique
    let creditAccounts = this.credits.map(r => r.account.value)
    let debitAccounts = this.debits.map(r => r.account.value)
    let allAccounts = [...creditAccounts, ...debitAccounts]
    return result && (new Set(allAccounts)).size === allAccounts.length
  }

  onSave() {
    this.service.createBooking(this.book, {
      text: this.form.value.text!,
      tag: this.form.value.tag || undefined,
      description: this.form.value.description || undefined,
      date: this.form.value.date!,
      credits: this.credits.map(row => {
        return {accountId: row.account.value!.id, amount: new Big(row.amount.value!)}
      }),
      debits: this.debits.map(row => {
        return {accountId: row.account.value!.id, amount: new Big(row.amount.value!)}
      }),
    }).subscribe(id => {
      if (id > 0) {
        this.resetForm()
      }
    })
  }

  protected readonly AccountTypeUtil = AccountTypeUtil;
}

interface AccountRow {
  account: FormControl<AccountSummary | null>
  amount: FormControl<string | null>
  error?: string
}

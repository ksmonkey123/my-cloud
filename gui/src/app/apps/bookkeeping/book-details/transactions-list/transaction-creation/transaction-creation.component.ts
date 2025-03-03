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
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import Big from "big.js";
import {TranslocoDirective, TranslocoPipe} from "@jsverse/transloco";

@Component({
    selector: 'app-transaction-creation',
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
    MatMiniFabButton,
    TranslocoPipe,
    TranslocoDirective,
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
  }

  removeCreditRow(index: number) {
    this.credits.splice(index, 1)
    this.onAmountChanged()
  }

  addDebitRow() {
    this.debits.push(this.createAccountRow())
  }

  removeDebitRow(index: number) {
    this.debits.splice(index, 1)
    this.onAmountChanged()
  }

  onAmountChanged() {
    const creditSum = this.credits
      .map(row => new Big(row.amount.value || 0))
      .reduce((a, b) => a.plus(b))
    const debitSum = this.debits.filter((_, index) => index > 0)
      .map(row => new Big(row.amount.value || 0))
      .reduce((a, b) => a.plus(b), Big(0))
    this.debits[0].amount.setValue(creditSum.minus(debitSum).toFixed(2))
  }

  resetForm() {
    this.form.reset()
    this.credits = [this.createAccountRow()]
    this.debits = [this.createAccountRow(true)]
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

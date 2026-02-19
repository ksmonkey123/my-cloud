import {Component, effect, Input, signal} from '@angular/core';
import {BookkeepingService} from "../../../bookkeeping.service";
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
import {TranslocoDirective, TranslocoPipe} from "@jsverse/transloco";
import {AccountSummary, Book} from "../../../model/book";
import {AccountTypeUtil} from "../../../model/accountType";
import {MatAutocomplete, MatAutocompleteTrigger} from "@angular/material/autocomplete";
import {startWith} from "rxjs";
import {toSignal} from "@angular/core/rxjs-interop";

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
    MatAutocomplete,
    MatAutocompleteTrigger,
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

  filteredOptions = signal<string[]>([])
  changeEvent = toSignal(this.form.controls.tag.valueChanges.pipe(startWith('')))

  constructor(private service: BookkeepingService) {
    effect(() => {
      this.filteredOptions.set(
        this.updateFilter(
          this.service.tags$(),
          this.changeEvent() || '',
        )
      )
    });

  }

  private updateFilter(options: string[], filter: string): string[] {
    const filterValue = filter.toLowerCase();
    return options.filter(option => option.toLowerCase().includes(filterValue));
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
      .map(row => Number(row.amount.value || 0))
      .reduce((a, b) => a + b)
    const debitSum = this.debits.filter((_, index) => index > 0)
      .map(row => Number(row.amount.value || 0))
      .reduce((a, b) => a + b, 0)
    this.debits[0].amount.setValue((creditSum - debitSum).toFixed(2))
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
        return {accountId: row.account.value!.id, amount: Number(row.amount.value)}
      }),
      debits: this.debits.map(row => {
        return {accountId: row.account.value!.id, amount: Number(row.amount.value!)}
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

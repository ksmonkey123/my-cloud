import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router, RouterLink} from "@angular/router";
import {AccountType, Book, BookkeepingService} from "../bookkeeping.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {
  MatDatepicker,
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatChip} from "@angular/material/chips";
import {MatDivider} from "@angular/material/divider";
import {MatTab, MatTabContent, MatTabGroup} from "@angular/material/tabs";
import {AccountGroupListComponent} from "./account-group-list/account-group-list.component";
import {TransactionsListComponent} from "./transactions-list/transactions-list.component";
import {ReportsComponent} from "./reports/reports.component";

@Component({
  selector: 'app-book-details',
  standalone: true,
  providers: [provideNativeDateAdapter()],
  imports: [
    MatButton,
    MatIcon,
    RouterLink,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    FormsModule,
    ReactiveFormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatFormFieldModule,
    MatDatepicker,
    MatDateRangeInput,
    MatDatepickerToggle,
    MatDateRangePicker,
    MatStartDate,
    MatEndDate,
    MatCardActions,
    MatChip,
    MatDivider,
    MatTabGroup,
    MatTab,
    MatTabContent,
    AccountGroupListComponent,
    TransactionsListComponent,
    ReportsComponent,
  ],
  templateUrl: './book-details.component.html',
  styleUrl: './book-details.component.scss'
})
export class BookDetailsComponent implements OnInit, OnDestroy {

  public edit_mode: boolean = false
  public book: Book | null = null

  public form = new FormGroup({
      title: new FormControl<string>(""),
      description: new FormControl<string>(""),
      start: new FormControl<Date>(new Date()),
      end: new FormControl<Date>(new Date()),
    }
  )

  private bookSubscription

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: BookkeepingService,
  ) {
    this.bookSubscription = this.service.book$.subscribe((b) => {
      this.book = b
    })
  }

  ngOnInit() {
    this.route.params.subscribe((params: Params) => {
      let bookId = params['book_id']
      if (bookId === 'new') {
        this.edit_mode = true
        this.book = null
      } else {
        this.service.loadBook(bookId)
      }
    })
  }

  ngOnDestroy() {
    this.bookSubscription.unsubscribe()
  }

  editBook() {
    this.edit_mode = true;

    if (this.book != null) {
      this.form.setValue({
        title: this.book.summary.title,
        description: this.book.summary.description || null,
        start: this.book.summary.openingDate,
        end: this.book.summary.closingDate
      })
    }
  }

  cancelBookEdit() {
    this.edit_mode = false;
  }

  saveBookEdit() {
    if (this.book != null) {
      // edit mode
      this.service.editBook(this.book.id, this.form.value.title!!, this.form.value.description || null)
      this.edit_mode = false
    } else {
      // create mode
      this.service.createBook({
        title: this.form.value.title!!,
        description: this.form.value.description || undefined,
        openingDate: this.form.value.start!!.toISOString(),
        closingDate: this.form.value.end!!.toISOString(),
      }).subscribe(
        (id) => {
          if (id > 0) {
            this.router.navigate(['..', id], {relativeTo: this.route})
            this.edit_mode = false
          }
        }
      )
    }
  }

  protected readonly AccountType = AccountType;
}

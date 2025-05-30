import {Component, OnDestroy, OnInit} from '@angular/core';
import {ActivatedRoute, Params, Router, RouterLink} from "@angular/router";
import {BookkeepingService} from "../bookkeeping.service";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatCard, MatCardActions, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatFormFieldModule, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {
  MatDatepickerToggle,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatChip} from "@angular/material/chips";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {AccountGroupListComponent} from "./account-group-list/account-group-list.component";
import {TransactionsListComponent} from "./transactions-list/transactions-list.component";
import {MatDialog} from "@angular/material/dialog";
import {ReportPopupComponent} from "./report-popup/report-popup.component";
import {TranslocoDirective, TranslocoPipe} from "@jsverse/transloco";
import {MatProgressBar} from "@angular/material/progress-bar";
import {toDateString} from "../../../utils";
import {Book} from "../model/book";

@Component({
  selector: 'app-book-details',
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
    MatDateRangeInput,
    MatDatepickerToggle,
    MatDateRangePicker,
    MatStartDate,
    MatEndDate,
    MatCardActions,
    MatChip,
    MatTabGroup,
    MatTab,
    AccountGroupListComponent,
    TransactionsListComponent,
    TranslocoPipe,
    TranslocoDirective,
    MatProgressBar,
  ],
  templateUrl: './book-details.component.html',
  styleUrl: './book-details.component.scss'
})
export class BookDetailsComponent implements OnInit, OnDestroy {

  public edit_mode: boolean = false
  public book: Book | null = null
  public exportActive = false


  public form = new FormGroup({
      title: new FormControl<string>(""),
      description: new FormControl<string>(""),
      start: new FormControl<Date>(new Date()),
      end: new FormControl<Date>(new Date()),
      closed: new FormControl<boolean>(false),
    }
  )

  selectedTabIndex = new FormControl(0)

  private bookSubscription
  private exportSubscription

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private service: BookkeepingService,
    private dialog: MatDialog,
  ) {
    this.bookSubscription = this.service.book$.subscribe((b) => {
      this.book = b
    })
    this.exportSubscription = service.exportInProgress$.subscribe((inProgress) => {
      this.exportActive = inProgress
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
    this.route.queryParams.subscribe((params: Params) => {
      let tabName = params['tab']
      if (tabName) {
        this.onTabChange(tabNameToIndex(tabName))
      }
    })
  }

  ngOnDestroy() {
    this.bookSubscription.unsubscribe()
    this.exportSubscription.unsubscribe()
  }

  editBook() {
    this.edit_mode = true;

    if (this.book != null) {
      this.form.setValue({
        title: this.book.summary.title,
        description: this.book.summary.description || null,
        start: this.book.summary.openingDate,
        end: this.book.summary.closingDate,
        closed: this.book.summary.closed,
      })
    }
  }

  cancelBookEdit() {
    this.edit_mode = false;
  }

  saveBookEdit() {
    if (this.book != null) {
      // edit mode
      this.service.editBook(this.book.id, this.form.value.title!!, this.form.value.description || null, this.form.value.closed!!)
      this.edit_mode = false
    } else {
      // create mode
      this.service.createBook({
        title: this.form.value.title!!,
        description: this.form.value.description || undefined,
        openingDate: toDateString(this.form.value.start!!),
        closingDate: toDateString(this.form.value.end!!),
      }).subscribe(
        (id) => {
          if (id > 0) {
            void this.router.navigate(['..', id], {relativeTo: this.route})
            this.edit_mode = false
          }
        }
      )
    }
  }

  onReportBundle() {
    this.service.exportEarningsReport(this.book!!.id)
  }

  onAccountLedgerExport() {
    this.service.exportAccountLedgers(this.book!!.id)
  }

  onPartialReport() {
    const dialogRef = ReportPopupComponent.open(this.dialog, {groups: this.book!.groups})

    dialogRef.afterClosed().subscribe((result) => {
        this.service.exportPartialEarningsReport(this.book!.id, result!)
      }
    )
  }

  onExcelExport() {
    this.service.exportTransactions(this.book!.id)
  }

  onTabChange(index: number) {
    this.selectedTabIndex.setValue(index)
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {tab: tabIndexToName(index)},
    })
  }
}

function tabNameToIndex(name: string): number {
  switch (name) {
    case 'transactions':
      return 1
    case 'reports' :
      return 2
    default:
      return 0
  }
}

function tabIndexToName(index: number): string | undefined {
  switch (index) {
    case 0:
      return 'accounts'
    case 1:
      return 'transactions'
    case 2:
      return 'reports'
    default:
      return undefined
  }
}

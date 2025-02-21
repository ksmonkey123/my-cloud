import {Component, OnDestroy} from '@angular/core';
import {AccountSummary, Book, BookingRecord, BookkeepingService, MoneyUtil} from "../../bookkeeping.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {TransactionDetailsComponent} from "./transaction-details/transaction-details.component";
import {TransactionCreationComponent} from "./transaction-creation/transaction-creation.component";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatPaginator, PageEvent} from "@angular/material/paginator";
import {MatProgressBar} from "@angular/material/progress-bar";

@Component({
    selector: 'app-transactions-list',
  imports: [
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRow,
    MatRowDef,
    MatIconButton,
    MatIcon,
    TransactionDetailsComponent,
    TransactionCreationComponent,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatPaginator,
    MatButton,
    MatProgressBar,
  ],
    animations: [
        trigger('detailExpand', [
            state('collapsed,void', style({ height: '0px', minHeight: '0' })),
            state('expanded', style({ height: '*' })),
            transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
        ]),
    ],
    templateUrl: './transactions-list.component.html',
    styleUrl: './transactions-list.component.scss'
})
export class TransactionsListComponent implements OnDestroy {

  page = 0
  public book: Book | null = null

  pageData: BookingRecord[] = []
  totalItems = 0
  accounts: { [id: string]: AccountSummary } = {}

  columnsToDisplay = ['id', 'date', 'text', 'amount']
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand']

  expandedElementIds: number[] = []

  exportActive: boolean = false

  bookSubscription
  elementSubscription
  accountsSubscription
  exportSubscription

  constructor(
    private service: BookkeepingService,
  ) {
    this.bookSubscription = this.service.book$.subscribe((b) => {
      this.book = b
      this.service.loadBookings(b!.id, this.page)
    })
    this.elementSubscription = service.bookingPage$.subscribe((page) => {
      this.pageData = page?.items || []
      this.expandedElementIds = []
      this.totalItems = page?.totalElements || 0
    })
    this.accountsSubscription = service.book$.subscribe((book) => {
      if (book) {
        let accountMap: { [id: string]: AccountSummary } = {}
        book.groups.flatMap(g => g.accounts).forEach(acc => accountMap[acc.id] = acc)
        this.accounts = accountMap
      }
    })
    this.exportSubscription = service.exportInProgress$.subscribe((inProgress) => {
      this.exportActive = inProgress
    })
  }

  ngOnDestroy() {
    this.bookSubscription.unsubscribe()
    this.elementSubscription.unsubscribe()
    this.accountsSubscription.unsubscribe()
    this.exportSubscription.unsubscribe()
  }

  toggleExpansion(id: number) {
    if (this.expandedElementIds.includes(id)) {
      this.expandedElementIds = this.expandedElementIds.filter(item => item != id)
    } else {
      this.expandedElementIds.push(id)
    }
  }

  onExport() {
    this.service.exportTransactions(this.book!.id)
  }

  protected readonly MoneyUtil = MoneyUtil;

  handlePageEvent(event: PageEvent) {
    this.service.loadBookings(this.book!.id, event.pageIndex, event.pageSize)
  }
}

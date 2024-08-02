import {Component, Input, OnDestroy, OnInit} from '@angular/core';
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
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {TransactionDetailsComponent} from "./transaction-details/transaction-details.component";
import {TransactionCreationComponent} from "./transaction-creation/transaction-creation.component";
import {MatDivider} from "@angular/material/divider";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";

@Component({
  selector: 'app-transactions-list',
  standalone: true,
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
    MatDivider,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
  ],
  animations: [
    trigger('detailExpand', [
      state('collapsed,void', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
  templateUrl: './transactions-list.component.html',
  styleUrl: './transactions-list.component.scss'
})
export class TransactionsListComponent implements OnInit, OnDestroy {

  page = 0
  @Input() book !: Book

  pageData: BookingRecord[] = []
  totalItems = 0
  accounts: { [id: string]: AccountSummary } = {}

  columnsToDisplay = ['id', 'date', 'text', 'amount']
  columnsToDisplayWithExpand = [...this.columnsToDisplay, 'expand']

  expandedElementIds: number[] = []

  elementSubscription
  accountsSubscription

  constructor(
    private service: BookkeepingService,
  ) {
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
  }

  ngOnInit() {
    this.service.loadBookings(this.book.id, this.page)
  }

  ngOnDestroy() {
    this.elementSubscription.unsubscribe()
    this.accountsSubscription.unsubscribe()
  }

  toggleExpansion(id: number) {
    if (this.expandedElementIds.includes(id)) {
      this.expandedElementIds = this.expandedElementIds.filter(item => item != id)
    } else {
      this.expandedElementIds.push(id)
    }
    console.log(this.expandedElementIds)
  }

  protected readonly MoneyUtil = MoneyUtil;
}

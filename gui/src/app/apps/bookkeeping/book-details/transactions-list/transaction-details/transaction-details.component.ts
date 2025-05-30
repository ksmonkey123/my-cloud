import {Component, Input} from '@angular/core';
import {BookkeepingService,} from "../../../bookkeeping.service";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {SimpleModalService} from "../../../../../common/simple-modal/simple-modal.service";
import {TransactionPopupComponent} from "./transaction-popup/transaction-popup.component";
import {MatDialog} from "@angular/material/dialog";
import {MatChip} from "@angular/material/chips";
import {TranslocoPipe, TranslocoService} from "@jsverse/transloco";
import {AccountSummary, Book} from "../../../model/book";
import {BookingRecord} from "../../../model/bookingRecord";
import {AccountType, AccountTypeUtil} from "../../../model/accountType";
import {MoneyUtil} from "../../../model/moneyUtil";

@Component({
  selector: 'app-transaction-details',
  imports: [
    MatCard,
    MatCardContent,
    MatIcon,
    MatButton,
    MatChip,
    TranslocoPipe,
  ],
  templateUrl: './transaction-details.component.html',
  styleUrl: './transaction-details.component.scss'
})
export class TransactionDetailsComponent {

  @Input() book!: Book
  @Input() booking!: BookingRecord
  @Input() accounts !: { [id: string]: AccountSummary }

  constructor(
    private modal: SimpleModalService,
    private service: BookkeepingService,
    private dialog: MatDialog,
    private translation: TranslocoService
  ) {
  }

  protected readonly AccountTypeUtil = AccountTypeUtil;
  protected readonly MoneyUtil = MoneyUtil;

  delete() {
    this.modal.confirm(
      this.translation.translate("bookkeeping.transactions.delete-modal.title"),
      this.translation.translate("bookkeeping.transactions.delete-modal.text", {id: this.booking.id})
    )
      .subscribe(confirm => {
        if (confirm) {
          this.service.deleteBooking(this.book, this.booking)
        }
      })
  }

  edit() {
    TransactionPopupComponent.open(this.dialog, this.booking)
      .afterClosed().subscribe(result => {
      if (result) {
        this.service.editBooking(this.book, this.booking.id, result)
      }
    })
  }

  protected readonly AccountType = AccountType;
}

import {Component, Input} from '@angular/core';
import {
  AccountSummary,
  AccountType,
  AccountTypeUtil,
  Book,
  BookingRecord,
  BookkeepingService,
  MoneyUtil
} from "../../../bookkeeping.service";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {SimpleModalService} from "../../../../../common/simple-modal/simple-modal.service";
import {TransactionPopupComponent} from "./transaction-popup/transaction-popup.component";
import {MatDialog} from "@angular/material/dialog";
import {MatChip} from "@angular/material/chips";

@Component({
  selector: 'app-transaction-details',
  imports: [
    MatCard,
    MatCardContent,
    MatIcon,
    MatButton,
    MatChip,
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
    private dialog: MatDialog
  ) {
  }

  protected readonly AccountTypeUtil = AccountTypeUtil;
  protected readonly MoneyUtil = MoneyUtil;

  delete() {
    this.modal.confirm("Delete Link", "Do you want to delete the booking record '" + this.booking.id + "'?")
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

import {Component, Input} from '@angular/core';
import {
  AccountSummary,
  AccountTypeUtil,
  Book,
  BookingRecord,
  BookkeepingService,
  MoneyUtil
} from "../../../bookkeeping.service";
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatTable} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {SimpleModalService} from "../../../../../common/simple-modal/simple-modal.service";

@Component({
  selector: 'app-transaction-details',
  standalone: true,
  imports: [
    MatCard,
    MatCardContent,
    MatTable,
    MatIcon,
    MatButton
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

  }

}

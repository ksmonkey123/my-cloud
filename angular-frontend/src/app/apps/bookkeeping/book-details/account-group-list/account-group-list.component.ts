import {Component, Input} from '@angular/core';
import {BookkeepingService} from "../../bookkeeping.service";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelActionRow,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatDialog} from "@angular/material/dialog";
import {GroupPopupComponent} from "./group-popup/group-popup.component";
import {SimpleModalService} from "../../../../common/simple-modal/simple-modal.service";
import {AccountPopupComponent} from "./account-popup/account-popup.component";
import {TranslocoPipe} from "@jsverse/transloco";
import {AccountGroup, AccountSummary, Book} from "../../model/book";
import {AccountTypeUtil} from "../../model/accountType";
import {MoneyUtil} from "../../model/moneyUtil";

@Component({
    selector: 'app-account-group-list',
  imports: [
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelTitle,
    MatExpansionPanelActionRow,
    MatExpansionPanelHeader,
    MatButton,
    MatIcon,
    TranslocoPipe,

  ],
    templateUrl: './account-group-list.component.html',
    styleUrl: './account-group-list.component.scss'
})
export class AccountGroupListComponent {

  @Input() public book!: Book

  public constructor(
    private service: BookkeepingService,
    private dialog: MatDialog,
    private modal: SimpleModalService,
  ) {
  }

  accountPopup(group: AccountGroup, account ?: AccountSummary) {
    const dialogRef = AccountPopupComponent.open(this.dialog, {
      groupNumber: group.groupNumber,
      data: account ? {
        title : account.title,
        description : account.description,
        type : account.accountType,
        id: account.id
      } : undefined,
    })
    dialogRef.afterClosed().subscribe((result) => {
      this.service.saveAccount(this.book.id, result!.id, result!.title, result!.description, result!.type, false)
    })
  }

  lockAccount(account: AccountSummary, newState: boolean) {
    this.service.saveAccount(this.book.id, account.id, account.title, account.description, account.accountType, newState)
  }

  groupPopup(group ?: AccountGroup) {
    const dialogRef = GroupPopupComponent.open(this.dialog, {
      data: group ? {
        number: group.groupNumber,
        title: group.title,
      } : undefined
    })
    dialogRef.afterClosed().subscribe((result) => {
      this.service.saveAccountGroup(this.book.id, result!.number, result!.title, false)
    })
  }

  lockGroup(group: AccountGroup, newState: boolean) {
    this.service.saveAccountGroup(this.book.id, group.groupNumber, group.title, newState)
  }

  deleteGroup(group: AccountGroup) {
    this.modal.confirm("Delete Account Group", "Do you want to delete the group '" + group.groupNumber + " - " + group.title + "'?")
      .subscribe(confirm => {
        if (confirm) {
          this.service.deleteGroup(this.book.id, group.groupNumber)
        }
      })
  }

  protected readonly AccountTypeUtil = AccountTypeUtil;

  deleteAccount(account: AccountSummary) {
    this.modal.confirm("Delete Account", "Do you want to delete the account '" + account.id + " - " + account.title + "'?")
      .subscribe(confirm => {
        if (confirm) {
          this.service.deleteAccount(this.book.id, account.id)
        }
      })
  }

  protected readonly MoneyUtil = MoneyUtil;
}

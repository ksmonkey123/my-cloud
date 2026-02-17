import {Component} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {MatCard, MatCardContent} from "@angular/material/card";
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
import {MatIcon} from "@angular/material/icon";
import {MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {Link, ShortenerService} from "./shortener.service";
import {Clipboard} from "@angular/cdk/clipboard";
import {MatSnackBar} from "@angular/material/snack-bar";
import {SimpleModalService} from "../../common/simple-modal/simple-modal.service";
import {MatDialog} from "@angular/material/dialog";
import {DialogResult, LinkEditPopupDialog} from "./link-edit-popup/link-edit-popup.dialog";
import {TranslocoDirective, TranslocoService} from "@jsverse/transloco";

@Component({
  selector: 'app-shortener',
  imports: [
    AsyncPipe,
    MatCard,
    MatCardContent,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatMiniFabButton,
    MatRow,
    MatRowDef,
    MatTable,
    MatHeaderCellDef,
    MatIconButton,
    TranslocoDirective
  ],
  templateUrl: './shortener.component.html',
  styleUrl: './shortener.component.scss'
})
export class ShortenerComponent {

  displayedColumns = ['id', 'clipboard', 'targetUrl', 'delete']

  public list$

  constructor(private service: ShortenerService,
              private clipboard: Clipboard,
              private snackbar: MatSnackBar,
              private modal: SimpleModalService,
              private dialog: MatDialog,
              private transloco: TranslocoService) {
    service.loadList()
    this.list$ = service.linkList$
  }

  deleteLink(link: Link) {
    this.modal.confirm(
      this.transloco.translate("shortener.delete.title"),
      this.transloco.translate("shortener.delete.text", link))
      .subscribe(confirm => {
        if (confirm) {
          this.service.deleteLink(link.id)
        }
      })
  }

  openNewLinkDialog() {
    const dialogRef = this.dialog.open(LinkEditPopupDialog)
    dialogRef.afterClosed().subscribe((result: DialogResult) => {
        if (result) {
          this.service.createLink(result.targetUrl)
        }
      }
    )
  }

  copyToClipboard(link: Link) {
    this.clipboard.copy(location.origin + '/s/' + link.id)
    this.snackbar.open(this.transloco.translate('shortener.link-copy'), 'OK', {duration: 2000})
  }

  protected readonly location = location;
}

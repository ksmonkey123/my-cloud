import {Component} from '@angular/core';
import {AsyncPipe} from "@angular/common";
import {MatCard, MatCardContent} from "@angular/material/card";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatIconButton, MatMiniFabButton} from "@angular/material/button";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {Link, ShortenerService} from "./shortener.service";
import {Clipboard} from "@angular/cdk/clipboard";
import {MatSnackBar} from "@angular/material/snack-bar";
import {MatTooltip} from "@angular/material/tooltip";
import {SimpleModalService} from "../../common/simple-modal/simple-modal.service";
import {MatDialog} from "@angular/material/dialog";
import {DialogResult, LinkEditPopupDialog} from "./link-edit-popup/link-edit-popup.dialog";

@Component({
  selector: 'app-shortener',
  standalone: true,
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
    MatSlideToggle,
    MatTable,
    MatHeaderCellDef,
    MatIconButton,
    MatTooltip
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
              private dialog: MatDialog) {
    service.loadList()
    this.list$ = service.linkList$
  }

  deleteLink(link: Link) {
    this.modal.confirm("Delete Link", "Do you want to delete the link '" + link.id + "'?")
      .subscribe(confirm => {
        if (confirm) {
          this.service.deleteLink(link.id)
        }
      })
  }

  openNewLinkDialog() {
    const dialogRef = this.dialog.open(LinkEditPopupDialog)
    dialogRef.afterClosed().subscribe((result: DialogResult) =>
      this.service.createLink(result.targetUrl)
    )
  }

  copyToClipboard(link: Link) {
    this.clipboard.copy(location.origin + '/s/' + link.id)
    this.snackbar.open('short link copied to clipboard', 'OK', {duration: 2000})
  }

  protected readonly location = location;
}

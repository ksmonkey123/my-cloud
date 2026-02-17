import {Injectable} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {SimpleModalDialog} from "./simple-modal.dialog";
import {map} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SimpleModalService {

  constructor(private dialog: MatDialog) {
  }

  confirm(title: string, content ?: string) {
    const dialogRef = this.dialog.open(SimpleModalDialog, {
      data: {title: title, content: content}
    })
    return dialogRef.afterClosed().pipe(map(x => !!x))
  }

}


export interface ModalConfig {
  title: string
  content?: string
}

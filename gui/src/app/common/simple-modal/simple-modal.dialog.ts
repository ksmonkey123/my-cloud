import {Component, Inject} from '@angular/core';
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {ModalConfig} from "./simple-modal.service";

@Component({
    selector: 'app-simple-modal',
    imports: [
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogTitle,
        MatDialogClose
    ],
    templateUrl: './simple-modal.dialog.html',
    styleUrl: './simple-modal.dialog.scss'
})
export class SimpleModalDialog {

  constructor(public dialogRef: MatDialogRef<SimpleModalDialog>,
              @Inject(MAT_DIALOG_DATA) public config: ModalConfig) {

  }

  onCancel() {
    this.dialogRef.close()
  }

  protected readonly confirm = confirm;
}

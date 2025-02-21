import {Component, Inject} from '@angular/core';
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";

@Component({
    selector: 'app-transaction-popup',
    imports: [
        FormsModule,
        MatButton,
        MatDialogActions,
        MatDialogContent,
        MatDialogTitle,
        MatFormField,
        MatInput,
        MatLabel,
        ReactiveFormsModule,
        MatDialogClose
    ],
    templateUrl: './transaction-popup.component.html',
    styleUrl: './transaction-popup.component.scss'
})
export class TransactionPopupComponent {

  form = new FormGroup(
    {
      text: new FormControl(''),
      tag: new FormControl(''),
      description: new FormControl(''),
    }
  )

  constructor(
    public dialogRef: MatDialogRef<TransactionPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public config: TransactionPopupData
  ) {
    this.form.setValue({
      text: config.text,
      description: config.description || null,
      tag: config.tag || null,
    })
  }

  collectData(): TransactionPopupData {
    return {
      text: this.form.value.text!,
      tag: this.form.value.tag || undefined,
      description: this.form.value.description || undefined,
    }
  }

  static open(
    dialog: MatDialog,
    config: TransactionPopupData
  ): MatDialogRef<TransactionPopupComponent, TransactionPopupData> {
    return dialog.open(TransactionPopupComponent, {data: config})
  }

}

export interface TransactionPopupData {
  text: string,
  description?: string,
  tag?: string,
}

import {Component} from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {MatButton} from "@angular/material/button";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {TranslocoPipe} from "@jsverse/transloco";

@Component({
    selector: 'app-link-edit-popup',
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    ReactiveFormsModule,
    TranslocoPipe
  ],
    templateUrl: './link-edit-popup.dialog.html',
    styleUrl: './link-edit-popup.dialog.scss'
})
export class LinkEditPopupDialog {

  form = this.formBuilder.group({
    targetUrl: '',
  })

  constructor(public dialogRef: MatDialogRef<LinkEditPopupDialog>,
              private formBuilder: FormBuilder
  ) {
  }

  collectData(): DialogResult {
      return { targetUrl: this.form.value.targetUrl || ''}
  }

}

export interface DialogResult {
  targetUrl: string
}

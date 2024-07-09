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
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-link-edit-popup',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    FormsModule,
    MatCard,
    MatCardContent,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    NgIf,
    ReactiveFormsModule
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

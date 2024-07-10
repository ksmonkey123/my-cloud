import {Component} from '@angular/core';
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatIcon} from "@angular/material/icon";
import {MatInput} from "@angular/material/input";
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {NgIf} from "@angular/common";

@Component({
  selector: 'app-add-user-popup',
  standalone: true,
  imports: [
    MatDialogTitle,
    MatDialogContent,
    FormsModule,
    MatFormField,
    MatIcon,
    MatInput,
    MatLabel,
    MatMiniFabButton,
    NgIf,
    ReactiveFormsModule,
    MatDialogActions,
    MatButton,
    MatDialogClose
  ],
  templateUrl: './add-user-popup.dialog.html',
  styleUrl: './add-user-popup.dialog.scss'
})
export class AddUserPopupDialog {

  constructor(
    public dialogRef: MatDialogRef<AddUserPopupDialog>,
    private formBuilder: FormBuilder
  ) {
  }

  form = this.formBuilder.group({
    username: '',
    password: ''
  })

  collectData() : DialogResult {
   return {
     username: this.form.value.username!,
     password: this.form.value.password!
   }
  }

}

export interface DialogResult {
  username: string,
  password: string,
}

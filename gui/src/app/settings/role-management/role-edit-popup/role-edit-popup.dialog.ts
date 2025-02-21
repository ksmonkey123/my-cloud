import {Component, Inject} from '@angular/core';
import {
  MAT_DIALOG_DATA,
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

@Component({
    selector: 'app-role-edit-popup',
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
    ReactiveFormsModule
  ],
    templateUrl: './role-edit-popup.dialog.html',
    styleUrl: './role-edit-popup.dialog.scss'
})
export class RoleEditPopupDialog {

  form = this.formBuilder.group({
    name: '',
    description: '',
  })
  createMode
  name

  constructor(public dialogRef: MatDialogRef<RoleEditPopupDialog>,
              @Inject(MAT_DIALOG_DATA) public config: DialogConfig,
              private formBuilder: FormBuilder
  ) {
    this.createMode = config.create
    if (!config.create) {
      this.form.setValue({
        name: config.name!,
        description: config.description || null,
      })
      this.form.get('name')?.disable()
      this.name = config.name
    }
  }

  collectData(): DialogResult {
    if (this.createMode) {
      return {name: this.form.value.name!, description: this.form.value.description || ''}
    } else {
      return {name: this.name!, description: this.form.value.description || ''}
    }
  }

}

export interface DialogConfig {
  create: boolean
  name?: string
  description?: string
}

export interface DialogResult {
  name: string,
  description: string
}

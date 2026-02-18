import {Component, Inject} from '@angular/core';
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
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatButton} from "@angular/material/button";
import {MatInput} from "@angular/material/input";
import {TranslocoPipe} from "@jsverse/transloco";

@Component({
    selector: 'app-group-popup',
  imports: [
    MatDialogContent,
    MatFormField,
    ReactiveFormsModule,
    MatDialogActions,
    MatButton,
    MatLabel,
    MatDialogClose,
    MatInput,
    MatDialogTitle,
    TranslocoPipe
  ],
    templateUrl: './group-popup.component.html',
    styleUrl: './group-popup.component.scss'
})
export class GroupPopupComponent {

  createMode: boolean = false

  form = new FormGroup({
    number: new FormControl<number | null>(null, [
      Validators.min(1),
      Validators.max(9),
    ]),
    title: new FormControl<string>('')
  })

  constructor(
    public dialogRef: MatDialogRef<GroupPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public config: GroupDialogConfig,
  ) {
    if (config.data != null) {
      this.createMode = false
      this.form.setValue({
        number: config.data.number,
        title: config.data.title,
      })
      this.form.controls.number.disable()
    } else {
      this.createMode = true
    }
  }

  collectData(): GroupDialogResult {
    return {
      number: this.createMode ? this.form.value.number! : this.config.data?.number!,
      title: this.form.value.title!,
    }
  }

  static open(
    dialog: MatDialog,
    config: GroupDialogConfig
  ) {
    return dialog.open<GroupPopupComponent, GroupDialogConfig, GroupDialogResult>(GroupPopupComponent, {data: config})
  }

}

export interface GroupDialogConfig {
  data?: {
    number: number,
    title: string,
  }
}

export interface GroupDialogResult {
  number: number,
  title: string,
}

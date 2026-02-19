import {Component} from '@angular/core';
import {TranslocoPipe} from "@jsverse/transloco";
import {
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {FormBuilder, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {Feature} from "../../../common/features.service";
import {MatSlideToggle} from "@angular/material/slide-toggle";

@Component({
  selector: 'app-feature-create-popup',
  imports: [
    TranslocoPipe,
    MatDialogTitle,
    MatDialogContent,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatSlideToggle
  ],
  templateUrl: './feature-create-popup.dialog.html',
  styleUrl: './feature-create-popup.dialog.scss'
})
export class FeatureCreatePopup {

  form = this.formBuilder.group({id: '', enabled: false})

  constructor(public dialogRef: MatDialogRef<FeatureCreatePopup>,
              private formBuilder: FormBuilder,
  ) {

  }

  collectData(): Feature {
    return {id: this.form.value.id!, enabled: this.form.value.enabled!}
  }

}

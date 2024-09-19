import {Component, Inject, model} from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import {AccountGroup, AccountType} from "../../bookkeeping.service";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {FormControl, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatInput} from "@angular/material/input";
import {MatOption, MatSelect} from "@angular/material/select";
import {MatButton} from "@angular/material/button";
import {MatCheckbox} from "@angular/material/checkbox";

@Component({
  selector: 'app-report-popup',
  standalone: true,
  imports: [
    MatDialogContent,
    MatDialogTitle,
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatLabel,
    MatSelect,
    MatOption,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatCheckbox,
    FormsModule
  ],
  templateUrl: './report-popup.component.html',
  styleUrl: './report-popup.component.scss'
})
export class ReportPopupComponent {

  public title = new FormControl("")
  public checkboxes: { group: AccountGroup, value: FormControl<boolean | null> }[] = []

  constructor(
    public dialogRef: MatDialogRef<ReportPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public config: ReportDialogConfig
  ) {
    for (let group of config.groups) {
      const hasExpenseAccount = group.accounts.some((account) => account.accountType === AccountType.INCOME || account.accountType === AccountType.EXPENSE)

      const fc = new FormControl(hasExpenseAccount)
      if (!hasExpenseAccount) {
        fc.disable()
      }
      this.checkboxes.push({group: group, value: fc})
    }
  }

  static open(
    dialog: MatDialog,
    config: ReportDialogConfig
  ) {
    return dialog.open<ReportPopupComponent, ReportDialogConfig, ReportDialogResult>(ReportPopupComponent, {data: config})
  }

  collectData(): ReportDialogResult {
    const numbers: number[] = []

    for (let box of this.checkboxes) {
      if (box.value.value) {
        numbers.push(box.group.groupNumber)
      }
    }

    return {
      title: this.title.value!,
      groupNumber: numbers,
    }
  }
}


export interface ReportDialogResult {
  title: string,
  groupNumber: number[]
}

export interface ReportDialogConfig {
  groups: AccountGroup[]
}

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
import {MatOption, MatSelect} from "@angular/material/select";
import {AccountType, AccountTypeUtil} from "../../../bookkeeping.service";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-account-popup',
  standalone: true,
  imports: [
    MatDialogContent,
    MatFormField,
    ReactiveFormsModule,
    MatDialogActions,
    MatButton,
    MatDialogClose,
    MatInput,
    MatDialogTitle,
    MatLabel,
    MatSelect,
    MatOption,
    MatIcon,
  ],
  templateUrl: './account-popup.component.html',
  styleUrl: './account-popup.component.scss'
})
export class AccountPopupComponent {
  createMode: boolean = false

  types = [
    AccountType.ASSET,
    AccountType.LIABILITY,
    AccountType.INCOME,
    AccountType.EXPENSE
  ]

  form = new FormGroup({
    number: new FormControl<number | null>(null,
      [
        Validators.min(1),
        Validators.max(999),
      ]),
    title: new FormControl<string>(''),
    description: new FormControl<string>(''),
    type: new FormControl<AccountType | null>(null),
  })

  constructor(
    public dialogRef: MatDialogRef<AccountPopupComponent>,
    @Inject(MAT_DIALOG_DATA) public config: AccountDialogConfig,
  ) {
    if (config.data != null) {
      this.createMode = false
      this.form.setValue({
        title: config.data.title,
        description: config.data.description || null,
        type: config.data.type,
        number: AccountPopupComponent.extractNumberFromId(config.data.id)
      })
      this.form.controls.type.disable()
      this.form.controls.number.disable()
    } else {
      this.createMode = true
    }
  }

  private static extractNumberFromId(id: string): number {
    return parseInt(id.split('.')[1])
  }

  private static buildId(groupNumber: number, accountId: number): string {
    return groupNumber + '.' + accountId.toString().padStart(3, '0')
  }

  collectData(): AccountDialogResult | undefined {
    if (!this.form.valid)
      return undefined

    if (this.createMode) {
      return {
        id: AccountPopupComponent.buildId(this.config.groupNumber, this.form.value.number!),
        title: this.form.value.title!,
        description: this.form.value.description || undefined,
        type: this.form.value.type!
      }
    } else {
      return {
        id: this.config.data!.id,
        type: this.config.data!.type,
        title: this.form.value.title!,
        description: this.form.value.description || undefined,
      }
    }
  }

  static open(
    dialog: MatDialog,
    config: AccountDialogConfig
  ) {
    return dialog.open<AccountPopupComponent, AccountDialogConfig, AccountDialogResult>(AccountPopupComponent, {data: config})
  }


  protected readonly AccountType = AccountType;
  protected readonly AccountTypeUtil = AccountTypeUtil;
}

export interface AccountDialogConfig {
  groupNumber: number,
  data?: {
    id: string,
    title: string,
    description?: string,
    type: AccountType,
  }
}

export interface AccountDialogResult {
  id: string,
  title: string,
  description?: string,
  type: AccountType,
}

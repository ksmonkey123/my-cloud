import {Component} from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators
} from "@angular/forms";
import {AuthService} from "../../common/auth.service";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatIcon} from "@angular/material/icon";
import {MatButton} from "@angular/material/button";
import {ToastrService} from "ngx-toastr";
import {TranslocoDirective, TranslocoService} from "@jsverse/transloco";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import {MatOption} from "@angular/material/autocomplete";
import {MatSelect} from "@angular/material/select";
import {LanguageCode, languages} from "../../common/language.model";

@Component({
  selector: 'app-password-change',
  imports: [
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatLabel,
    MatIcon,
    MatButton,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    TranslocoDirective,
    MatOption,
    MatSelect,

  ],
  templateUrl: './account-settings.component.html',
  styleUrl: './account-settings.component.scss'
})
export class AccountSettingsComponent {

  constructor(protected authService: AuthService, private toastr: ToastrService, private transloco: TranslocoService) {
  }

  pwForm = new FormGroup({
    'oldPassword': new FormControl(null, [Validators.required]),
    'newPassword': new FormControl(null, [Validators.required]),
    'confirmPassword': new FormControl(null, [Validators.required, (control: AbstractControl): ValidationErrors | null => {
      if (control.value !== this.pwForm?.value.newPassword) {
        return {badConfirm: true}
      } else {
        return null
      }
    }]),
  })

  onChangePassword() {
    this.authService.changePassword(this.pwForm.value.oldPassword!, this.pwForm.value.newPassword!).subscribe(
      {
        next: () => {
          this.toastr.success(this.transloco.translate("settings.account.password.changed"))
        },
        error: (error) => {
          this.toastr.error(error?.error?.message, this.transloco.translate("settings.account.password.failed"))
        }
      }
    )
  }

  protected readonly languages = languages;

  onLanguageChange(code: LanguageCode) {
    this.authService.changeLanguage(code).subscribe({
        next: () => {
          this.toastr.success(this.transloco.translate("settings.account.settings-changed"))
          this.authService.fetch()
        },
        error: (error) => {
          this.toastr.error(error?.error?.message, this.transloco.translate("settings.account.change-error"))
        }
      }
    )
  }
}

import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader} from "@angular/material/card";
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

@Component({
    selector: 'app-password-change',
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatFormField,
    ReactiveFormsModule,
    MatInput,
    MatLabel,
    MatIcon,
    MatButton,

  ],
    templateUrl: './account-settings.component.html',
    styleUrl: './account-settings.component.scss'
})
export class AccountSettingsComponent {

  constructor(private authService: AuthService, private toastr: ToastrService) {
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
          this.toastr.success("Password Changed")
        },
        error: (error) => {
          this.toastr.error(error?.error?.message, "Password Change failed", {enableHtml: true})
        }
      }
    )
  }

}

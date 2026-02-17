import {Component, OnInit} from '@angular/core';
import {MatCard, MatCardContent} from "@angular/material/card";
import {FormBuilder, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {AuthService} from "../common/auth.service";
import {ActivatedRoute, Router} from "@angular/router";

import {MatCheckbox} from "@angular/material/checkbox";

@Component({
    selector: 'app-login',
  imports: [
    MatCard,
    MatCardContent,
    FormsModule,
    MatFormField,
    MatInput,
    MatLabel,
    MatButton,
    MatIcon,
    ReactiveFormsModule,
    MatCheckbox
],
    templateUrl: './login.component.html',
    styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
  }

  problem = false

  form = this.formBuilder.group({
    username: '',
    password: '',
    longRetention: false,
  })

  private target: string | undefined;

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
        this.target = params['target']
        if (this.authService.hasToken()) {
          this.navigateAfterLogin()
        }
      }
    )
  }

  onSubmit() {
    this.authService.login(this.form.value.username || '', this.form.value.password || '', this.form.value.longRetention || false)
      .subscribe({
          next: _ => {
            this.navigateAfterLogin()
          },
          error: () => {
            this.problem = true
          }
        }
      )
  }

  private navigateAfterLogin() {
      void this.router.navigateByUrl(this.target || '/')
  }

}

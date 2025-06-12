import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {TranslocoService} from "@jsverse/transloco";
import {LanguageCode} from "../../common/language.model";

@Injectable()
export class UserManagementService implements OnDestroy {

  constructor(private http: HttpClient, private toastr: ToastrService, private translation: TranslocoService) {
  }

  public accountList$ = new BehaviorSubject<Account[]>([])
  public userDetails$ = new BehaviorSubject<AccountDetails | undefined>(undefined)


  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.userDetails$.complete()
    this.accountList$.complete()
  }

  loadList() {
    this.http.get<Account[]>('rest/auth/accounts')
      .pipe(takeUntil(this.closer$))
      .subscribe((x) => this.accountList$.next(x))
  }

  createUser(username: string, password: string, language: string) {
    return this.http.put('rest/auth/accounts/' + username, {
      username: username,
      password: password,
      languageCode: language,
    })
  }

  loadUser(username: string) {
    this.http.get<AccountDetails>('/rest/auth/accounts/' + username)
      .pipe(takeUntil(this.closer$))
      .subscribe((account) => this.userDetails$.next(account))
  }

  setUserEnabled(username: string, enabled: boolean) {
    return this.http.patch('rest/auth/accounts/' + username, {
      enabled: enabled
    })
  }

  changeUserLanguage(username: string, language: LanguageCode) {
    this.http.patch('rest/auth/accounts/' + username, {
      languageCode: language
    }).pipe(takeUntil(this.closer$))
      .subscribe({
          next: _ => {
            this.toastr.success(this.translation.translate(this.translation.translate("settings.users.language-changed")))
          },
          error: error => {
            this.toastr.error(error?.error?.message, this.translation.translate("settings.users.error.edit", {id: username}))
          }
        }
      )
  }

  setBasicsForUser(username: string, enabled?: boolean, admin?: boolean) {
    this.http.patch<Account>('/rest/auth/accounts/' + username, {
      enabled: enabled,
      admin: admin
    })
      .pipe(takeUntil(this.closer$))
      .subscribe({
          next: _ => this.loadUser(username),
          error: error => {
            this.toastr.error(error?.error?.message,)
            this.loadUser(username)
          }
        }
      )
  }

  setRoleStateForUser(username: string, role: string, newState: boolean) {
    this.http.patch<AccountDetails>('/rest/auth/accounts/' + username + '/roles', {
      add: newState ? [role] : null,
      remove: newState ? null : [role]
    })
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (acc) => {
          this.userDetails$.next(acc)
        },
        error: (error) => {
          this.toastr.error(error?.error?.message, this.translation.translate("settings.users.error.creation", {id: username}))
          this.loadUser(username)
        }
      })
  }

  resetPassword(username: string, password: string) {
    this.http.patch<Account>('/rest/auth/accounts/' + username, {password: password})
      .pipe(takeUntil(this.closer$))
      .subscribe({
          next: _ => {
            this.toastr.success(this.translation.translate(this.translation.translate("settings.users.password-changed")))
          },
          error: error => {
            this.toastr.error(error?.error?.message, this.translation.translate("settings.users.error.edit", {id: username}))
          }
        }
      )
  }

}

export interface Account {
  username: string
  admin: boolean
  enabled: boolean
  email?: string
}

export interface AccountDetails {
  username: string
  admin: boolean
  enabled: boolean
  roles: string[]
  languageCode: LanguageCode
}

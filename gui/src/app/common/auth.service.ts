import {Injectable, OnDestroy, signal} from '@angular/core';
import {map, Observable, Subject, takeUntil} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {LanguageCode} from "./language.model";
import {TranslocoService} from "@jsverse/transloco";

export const TOKEN_NAME = "auth_token"

export interface AuthInfo {
  username: string
  admin: boolean
  roles: string[]
  languageCode: LanguageCode
}

interface AuthInfoDto {
  username: string
  roles: string[]
  languageCode: LanguageCode
}

@Injectable({
  providedIn: 'root'
})
export class AuthService implements OnDestroy {

  public readonly authInfo = signal<AuthInfo | undefined>(undefined);

  private closer$ = new Subject<void>()

  constructor(private http: HttpClient, private translation: TranslocoService) {
  }

  hasToken() {
    return localStorage.getItem(TOKEN_NAME) !== null
  }

  getToken() {
    return localStorage.getItem(TOKEN_NAME)
  }

  setToken(token: string | null) {
    if (token === null) {
      localStorage.removeItem(TOKEN_NAME)
    } else {
      localStorage.setItem(TOKEN_NAME, token)
    }
  }

  public fetch() {
    this.http.get<AuthInfoDto>('rest/auth/authenticate')
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (user) => {
          const newInfo: AuthInfo = {
            username: user.username,
            roles: user.roles,
            admin: user.roles.includes("admin"),
            languageCode: user.languageCode,
          };
          this.authInfo.set(newInfo);
          this.translation.setActiveLang(newInfo.languageCode)
        }
      })
  }

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
  }

  login(username: string, password: string): Observable<boolean> {
    return this.http.post('rest/auth/login', {
      username: username,
      password: password
    })
      .pipe(
        map((response: any) => {
          this.setToken(response.token)
          this.fetch()
          return true
        }))
  }

  changePassword(oldPassword: string, newPassword: string) {
    return this.http.patch<void>('rest/auth/account/password', {
      oldPassword: oldPassword,
      newPassword: newPassword
    })
  }

  changeLanguage(language: LanguageCode) {
    return this.http.patch<void>('rest/auth/account', {
      languageCode: language
    })
  }

  fullLogout(callback: () => void) {
    this.http.post('rest/auth/logout', null).subscribe(
      {
        next: () => {
          this.logout()
          callback()
        },
        error: () => {
          this.logout()
          callback()
        }
      })
  }

  logout(): void {
    this.setToken(null)
  }

}

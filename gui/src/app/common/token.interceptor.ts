import {Injectable} from "@angular/core";
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import {Router} from "@angular/router";
import {AuthService} from "./auth.service";
import {catchError, EMPTY, Observable, throwError} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptor implements HttpInterceptor {

  constructor(private router: Router, public auth: AuthService) {

  }

  logout() {
    this.auth.logout()
    void this.router.navigate(['login'], {queryParams: {target: this.router.url}})
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (request.url !== 'rest/auth/login') {
      // auth/login is unauthenticated, everything else uses access token
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${this.auth.getToken()}`
        }
      });
      return next.handle(request).pipe(
        catchError(
          error => {
            if (error.status === 401) {
              this.logout();
              return EMPTY;
            } else {
              return throwError(() => error);
            }
          }
        )
      );
    } else {
      // auth/login uses default behaviour
      return next.handle(request);
    }
  }

}

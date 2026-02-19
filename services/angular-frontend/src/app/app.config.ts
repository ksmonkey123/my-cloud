import {ApplicationConfig, isDevMode} from '@angular/core';
import {provideRouter, withHashLocation} from '@angular/router';

import {routes} from './app.routes';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {provideHttpClient, withInterceptorsFromDi} from "@angular/common/http";
import {provideTokenInterceptor} from "./common/token.interceptor";
import {provideContentTypeInterceptor} from "./common/contenttype.interceptor";
import {provideToastr} from "ngx-toastr";
import {TranslocoHttpLoader} from './transloco-loader';
import {provideTransloco} from '@jsverse/transloco';
import {providePaginatorI18n} from "./common/paginator-i18n.provider";

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withHashLocation()),
    provideHttpClient(withInterceptorsFromDi()),
    provideTokenInterceptor(),
    provideContentTypeInterceptor(),
    provideToastr(),
    provideHttpClient(),
    provideTransloco({
      config: {
        availableLangs: ['de', 'en'],
        defaultLang: 'en',
        // Remove this option if your application doesn't support changing language in runtime.
        reRenderOnLangChange: true,
        prodMode: !isDevMode(),
      },
      loader: TranslocoHttpLoader
    }),
    providePaginatorI18n(),
  ]
};

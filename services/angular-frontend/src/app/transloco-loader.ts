import {inject, Injectable} from "@angular/core";
import {Translation, TranslocoLoader} from "@jsverse/transloco";
import {HttpClient} from "@angular/common/http";

import checksums from '../../i18n-checksums.json'
import {LanguageCode} from "./common/language.model";

@Injectable({providedIn: 'root'})
export class TranslocoHttpLoader implements TranslocoLoader {
  private http = inject(HttpClient);

  getTranslation(lang: LanguageCode) {
    return this.http.get<Translation>(`/assets/i18n/${lang}.json?v=${checksums[lang]}`);
  }
}

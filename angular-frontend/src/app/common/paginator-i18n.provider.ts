import {ClassProvider, Injectable, OnDestroy, OnInit} from "@angular/core";
import {MatPaginatorIntl} from "@angular/material/paginator";
import {Subject, Subscription} from "rxjs";
import {TranslocoService} from "@jsverse/transloco";

@Injectable()
export class PaginatorI18n implements MatPaginatorIntl, OnDestroy, OnInit {
  changes = new Subject<void>();

  firstPageLabel: string = "?first-page?"
  itemsPerPageLabel = "?items-per-page?";
  lastPageLabel = "?last-page-label?";
  nextPageLabel = "?next-page?";
  previousPageLabel = "?previous-page?";

  private langChangeSub: Subscription | undefined;

  constructor(private translate: TranslocoService) {
    this.calculateLabels()
  }

  private calculateLabels() {
    this.firstPageLabel = this.translate.translate("paginator.first-page")
    this.itemsPerPageLabel = this.translate.translate("paginator.items-per-page")
    this.lastPageLabel = this.translate.translate("paginator.last-page")
    this.nextPageLabel = this.translate.translate("paginator.next-page")
    this.previousPageLabel = this.translate.translate("paginator.previous-page")
    // notify of new labels
    this.changes.next()
  }

  getRangeLabel(page: number, pageSize: number, length: number): string {
    if (length === 0) {
      return this.translate.translate("paginator.current-page", {current: 1, size: 1})
    }
    const amountPages = Math.ceil(length / pageSize);

    return this.translate.translate("paginator.current-page", {current: page + 1, size: amountPages})
  }

  ngOnInit() {
    this.langChangeSub = this.translate.langChanges$.subscribe((newLang) => {
      this.calculateLabels()
    })
  }

  ngOnDestroy() {
    this.langChangeSub?.unsubscribe()
  }
}

export function providePaginatorI18n(): ClassProvider {
  return {provide: MatPaginatorIntl, useClass: PaginatorI18n}
}

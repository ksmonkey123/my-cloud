import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {TranslocoService} from "@jsverse/transloco";

@Injectable()
export class ShortenerService implements OnDestroy {

  constructor(private http: HttpClient, private toastr: ToastrService, private translation: TranslocoService) {
  }

  public linkList$ = new BehaviorSubject<Link[]>([])
  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.linkList$.complete()
  }

  loadList() {
    this.http.get<Link[]>('/rest/shortener/links')
      .pipe(takeUntil(this.closer$))
      .subscribe((l) => this.linkList$.next(l))
  }

  deleteLink(id: string) {
    this.http.delete('/rest/shortener/links/' + id)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: _ => {
          this.loadList()
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("shortener.error.deletion", {id: id}))
          this.loadList()
        }
      })
  }

  createLink(targetUrl: string) {
    this.http.post<Link>('/rest/shortener/links', {targetUrl: targetUrl})
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (link) => {
          this.toastr.success(
            this.translation.translate("shortener.created.title"),
            this.translation.translate("shortener.created.text", {id: link.id})
          )
          this.loadList()
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("shortener.error.creation"))
          this.loadList()
        }
      })
  }
}

export interface Link {
  id: string,
  targetUrl: string,
}

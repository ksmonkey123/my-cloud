import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {ToastrService} from "ngx-toastr";

@Injectable()
export class ShortenerService implements OnDestroy {

  constructor(private http: HttpClient, private toastr : ToastrService) {
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
          this.toastr.error(error?.error?.message, "could not delete link")
          this.loadList()
        }
      })
  }

  createLink(targetUrl: string) {
    this.http.post<Link>('/rest/shortener/links', { targetUrl: targetUrl })
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (link) => {
          this.toastr.success("Link Created", "Link Created with ID " + link.id)
          this.loadList()
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not create link")
          this.loadList()
        }
      })
  }
}

export interface Link {
  id : string,
  targetUrl: string,
}

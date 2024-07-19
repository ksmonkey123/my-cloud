import {Injectable, OnDestroy} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";

@Injectable()
export class BookkeepingService implements OnDestroy {


  constructor(private http: HttpClient, private toastr: ToastrService) {
  }

  public bookList$ = new BehaviorSubject<BookSummary[]>([])
  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.bookList$.complete()
  }

  loadBooks() {
    this.http.get<BookSummaryDto[]>('/rest/bookkeeping/books')
      .pipe(takeUntil(this.closer$))
      .subscribe(
        (l) => {
          this.bookList$.next(l.map(dto => ({
            id: dto.id,
            title: dto.title,
            description: dto.description,
            openingDate: new Date(dto.openingDate),
            closingDate: new Date(dto.closingDate)
          })))
        }
      )
  }

}

interface BookSummaryDto {
  id: string,
  title: string,
  description?: string,
  openingDate: string,
  closingDate: string,
}

export interface BookSummary {
  id: string,
  title: string,
  description?: string,
  openingDate: Date,
  closingDate: Date,
}

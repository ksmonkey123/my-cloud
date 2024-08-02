import {Injectable, OnDestroy} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {Account} from "../../settings/user-management/user-management.service";
import {Link} from "../shortener/shortener.service";

@Injectable()
export class BookkeepingService implements OnDestroy {


  constructor(private http: HttpClient, private toastr: ToastrService) {
  }

  public bookList$ = new BehaviorSubject<BookSummary[]>([])
  public book$ = new BehaviorSubject<Book | null>(null)

  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.bookList$.complete()
    this.book$.complete()
  }

  loadBooks() {
    this.http.get<BookSummaryDto[]>('/rest/bookkeeping/books')
      .pipe(takeUntil(this.closer$))
      .subscribe(
        (l) => {
          this.bookList$.next(l.map(this.mapBookSummaryDto))
        }
      )
  }

  private mapBookSummaryDto(dto: BookSummaryDto): BookSummary {
    return {
      id: dto.id,
      title: dto.title,
      description: dto.description,
      openingDate: new Date(dto.openingDate),
      closingDate: new Date(dto.closingDate)
    }
  }

  loadBook(id: number) {
    this.http.get<BookDto>('/rest/bookkeeping/books/' + id)
      .pipe(takeUntil(this.closer$))
      .subscribe(
        (dto) => {
          this.book$.next(
            {
              id: dto.id,
              summary: this.mapBookSummaryDto(dto.summary),
              groups: dto.groups
            }
          )
        }
      )
  }

  editBook(id: number, title: string, description: string | null) {
    this.http.put<BookSummaryDto>('/rest/bookkeeping/books/' + id, {
      title: title,
      description: description,
    })
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (book) => {
          this.toastr.success("Book Edited", "Book '" + book.title + "' edited successfully")
          this.loadBooks()
          this.loadBook(id)
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not edit book")
          this.loadBooks()
          this.loadBook(id)
        }
      })
  }

  createBook(request: CreateBookRequest) {
    let createdBookId$ = new BehaviorSubject<number>(0)
    this.http.post<BookSummaryDto>('/rest/bookkeeping/books', request)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (book) => {
          this.toastr.success("Book Created", "Book '" + book.title + "' created successfully")
          this.loadBooks()
          createdBookId$.next(book.id)
          createdBookId$.complete()
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not create book")
          this.loadBooks()
          createdBookId$.next(-1)
          createdBookId$.complete()
        }
      })
    return createdBookId$
  }

}

export interface CreateBookRequest {
  title: string,
  description?: string,
  openingDate: string,
  closingDate: string,
}

interface BookSummaryDto {
  id: number,
  title: string,
  description?: string,
  openingDate: string,
  closingDate: string,
}

interface BookDto {
  id: number,
  summary: BookSummaryDto,
  groups: AccountGroup[],
}

export interface Book {
  id: number,
  summary: BookSummary,
  groups: AccountGroup[],
}

export interface BookSummary {
  id: number,
  title: string,
  description?: string,
  openingDate: Date,
  closingDate: Date,
}

export interface AccountGroup {
  groupNumber: number,
  title: string,
  accounts: AccountSummary[]
}

export interface AccountSummary {
  id: String,
  title: string,
  description?: string,
  accountType: AccountType
}

export enum AccountType {
  ASSET = "ASSET",
  LIABILITY = "LIABILITY",
  EXPENSE = "EXPENSE",
  INCOME = "INCOME",
}

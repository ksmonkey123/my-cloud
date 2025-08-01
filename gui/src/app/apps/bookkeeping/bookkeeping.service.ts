import {Injectable, OnDestroy, signal} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import Big from "big.js";
import {TranslocoService} from "@jsverse/transloco";
import {toDateString} from "../../utils";
import {BookSummary} from "./model/bookSummary";
import {BookingMovement, BookingRecord} from "./model/bookingRecord";
import {AccountGroup, AccountSummary, Book} from "./model/book";
import {AccountType} from "./model/accountType";
import {DocumentIdentifier, handleDownload} from "../../common/documents/download-handler";

@Injectable()
export class BookkeepingService implements OnDestroy {


  constructor(private http: HttpClient, private toastr: ToastrService, private translation: TranslocoService) {
  }

  public book$ = new BehaviorSubject<Book | null>(null)
  public bookingPage$ = new BehaviorSubject<BookingPage | null>(null)
  public tags$ = signal<string[]>([])
  public exportInProgress$ = new BehaviorSubject<boolean>(false)

  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.book$.complete()
    this.bookingPage$.complete()
  }

  private mapBookSummaryDto(dto: BookSummaryDto): BookSummary {
    return {
      id: dto.id,
      title: dto.title,
      description: dto.description,
      openingDate: new Date(dto.openingDate),
      closingDate: new Date(dto.closingDate),
      closed: dto.closed,
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
              groups: dto.groups.map(group => {
                return {
                  title: group.title,
                  groupNumber: group.groupNumber,
                  locked: group.locked,
                  accounts: group.accounts.map(account => {
                    return {
                      id: account.id,
                      title: account.title,
                      description: account.description,
                      accountType: account.accountType,
                      locked: account.locked,
                      balance: new Big(account.balance)
                    }
                  })
                }
              })
            }
          )
        }
      )
  }

  editBook(id: number, title: string, description: string | null, closed: boolean) {
    this.http.put<BookSummaryDto>('/rest/bookkeeping/books/' + id, {
      title: title,
      description: description,
      closed: closed,
    })
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.loadBook(id)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.edit-failure"))
          this.loadBook(id)
        }
      })
  }

  exportTransactions(bookId: number) {
    this.doExport('/rest/bookkeeping/books/' + bookId + '/documents/export', {})
  }

  exportEarningsReport(bookId: number, config: {title: string, groupNumber: number[]} | null) {
    this.doExport('/rest/bookkeeping/books/' + bookId + '/documents/report', { params: config })
  }

  exportAccountLedgers(bookId: number) {
    this.doExport('/rest/bookkeeping/books/' + bookId + '/documents/ledgers', {})
  }

  private doExport(url: string, config: { params?: any, body?: any }) {
    this.exportInProgress$.next(true)
    this.http.post<DocumentIdentifier>(url, config.body, {
      params: config.params
    })
      .pipe(takeUntil(this.closer$))
      .subscribe({
          next: id => {
            handleDownload(id)
            this.exportInProgress$.next(false)
          },
          error: error => {
            this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.export-error"))
            this.exportInProgress$.next(false)
          }
        }
      )
  }

  createBook(request: CreateBookRequest) {
    let createdBookId$ = new BehaviorSubject<number>(0)
    this.http.post<BookSummaryDto>('/rest/bookkeeping/books', request)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (book) => {
          createdBookId$.next(book.id)
          createdBookId$.complete()
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.creation-failure"))
          createdBookId$.next(-1)
          createdBookId$.complete()
        }
      })
    return createdBookId$
  }

  saveAccountGroup(bookId: number, number: number, title: string, locked: boolean) {
    this.http.put<AccountGroup>('/rest/bookkeeping/books/' + bookId + '/groups/' + number, {
      title: title,
      locked: locked,
    }).pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.account-group.edit-failure", {id: number}))
          this.loadBook(bookId)
        }
      })
  }

  deleteGroup(bookId: number, groupNumber: number) {
    this.http.delete<any>('/rest/bookkeeping/books/' + bookId + '/groups/' + groupNumber)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.account-group.delete-failure", {id: groupNumber}))
          this.loadBook(bookId)
        }
      })
  }

  saveAccount(bookId: number, accountId: string, title: string, description: string | undefined, type: AccountType, locked: boolean) {
    this.http.put<AccountSummary>('/rest/bookkeeping/books/' + bookId + '/accounts/' + accountId, {
        title: title,
        description: description,
        accountType: type,
        locked: locked,
      }
    ).pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.account.save-failure", {id: accountId}))
          this.loadBook(bookId)
        }
      })
  }

  deleteAccount(bookId: number, accountId: String) {
    this.http.delete<any>('/rest/bookkeeping/books/' + bookId + '/accounts/' + accountId)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.account.delete-failure", {id: accountId}))
          this.loadBook(bookId)
        }
      })
  }

  private bookingPageState = {
    bookId: 0,
    pageSize: 10,
    pageNo: 0
  }

  reloadBookings() {
    this.http.get<BookingPageDto>('/rest/bookkeeping/books/' + this.bookingPageState.bookId + '/records', {
      params: {
        page: this.bookingPageState.pageNo,
        pageSize: this.bookingPageState.pageSize,
      }
    }).pipe(takeUntil(this.closer$))
      .subscribe(
        page => {
          this.bookingPage$.next({
            totalElements: page.totalElements,
            items: page.items.map(record => ({
              id: record.id,
              date: new Date(record.bookingDate),
              tag: record.tag,
              text: record.text,
              description: record.description,
              amount: new Big(record.amount),
              credits: record.credits.map(i => ({
                accountId: i.accountId,
                amount: new Big(i.amount)
              })),
              debits: record.debits.map(i => ({
                accountId: i.accountId,
                amount: new Big(i.amount)
              }))
            }))
          })
        }
      )
    this.http.get<string[]>('/rest/bookkeeping/books/' + this.bookingPageState.bookId + '/tags')
      .pipe(takeUntil(this.closer$))
      .subscribe(
        list => {
          this.tags$.set(list)
        }
      )
  }

  editBooking(book: Book, bookingId: number, patch: {
    text: string,
    tag?: string,
    description?: string,
  }) {
    this.http.put<any>('/rest/bookkeeping/books/' + book.id + '/records/' + bookingId, patch)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.reloadBookings()
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.transactions.edit-failure", {id: bookingId}))
          this.reloadBookings()
        }
      })
  }

  createBooking(book: Book, booking: CreateBookingRecord): BehaviorSubject<number> {
    const result = new BehaviorSubject(0)
    this.http.post<any>('/rest/bookkeeping/books/' + book.id + '/records', this.mapBookingRecord(booking))
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.reloadBookings()
          result.next(1)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.transactions.create-failure"))
          this.reloadBookings()
          result.next(-1)
        }
      })
    return result
  }

  private mapBookingRecord(booking: CreateBookingRecord): CreateBookingRecordDto {
    return {
      bookingDate: toDateString(booking.date),
      tag: booking.tag,
      text: booking.text,
      description: booking.description,
      credits: booking.credits.map(c => {
        return {
          accountId: c.accountId,
          amount: c.amount.toFixed(2)
        }
      }),
      debits: booking.debits.map(c => {
        return {
          accountId: c.accountId,
          amount: c.amount.toFixed(2)
        }
      })
    }
  }

  loadBookings(bookId: number, pageId: number, pageSize: number = 10) {
    this.bookingPageState.bookId = bookId
    this.bookingPageState.pageNo = pageId
    this.bookingPageState.pageSize = pageSize
    this.reloadBookings()
  }

  deleteBooking(book: Book, booking: BookingRecord) {
    this.http.delete<any>('/rest/bookkeeping/books/' + book.id + '/records/' + booking.id)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.toastr.success("Record Deleted", "Record " + booking.id + " was successfully deleted")
          this.reloadBookings()
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("bookkeeping.transactions.delete-failure", {id: booking.id}))
          this.reloadBookings()
        }
      })
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
  closed: boolean,
}

interface BookDto {
  id: number,
  summary: BookSummaryDto,
  groups: AccountGroupDto[]
}

interface AccountGroupDto {
  groupNumber: number,
  title: string,
  locked: boolean,
  accounts: AccountSummaryDto[]
}

interface AccountSummaryDto {
  id: string,
  title: string,
  description?: string,
  accountType: AccountType,
  locked: boolean,
  balance: any
}

interface BookingPageDto {
  items: BookingRecordDto[],
  totalElements: number,
}

interface BookingRecordDto {
  id: number,
  bookingDate: Date,
  tag?: string,
  text: string,
  description?: string,
  amount: any,
  credits: BookingMovementDto[],
  debits: BookingMovementDto[],
}

interface CreateBookingRecordDto {
  bookingDate: string,
  tag?: string,
  text: string,
  description?: string,
  credits: BookingMovementDto[],
  debits: BookingMovementDto[],
}

interface BookingMovementDto {
  accountId: string,
  amount: any,
}

export interface BookingPage {
  items: BookingRecord[],
  totalElements: number,
}

export interface CreateBookingRecord {
  date: Date,
  tag?: string,
  text: string,
  description?: string,
  credits: BookingMovement[],
  debits: BookingMovement[],
}

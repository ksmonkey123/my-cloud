import {Injectable, OnDestroy} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {ToastrService} from "ngx-toastr";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import Big from "big.js";

@Injectable()
export class BookkeepingService implements OnDestroy {


  constructor(private http: HttpClient, private toastr: ToastrService) {
  }

  public bookList$ = new BehaviorSubject<BookSummary[]>([])
  public book$ = new BehaviorSubject<Book | null>(null)
  public bookingPage$ = new BehaviorSubject<BookingPage | null>(null)

  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.bookList$.complete()
    this.book$.complete()
    this.bookingPage$.complete()
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

  saveAccountGroup(bookId: number, number: number, title: string) {
    this.http.put<AccountGroup>('/rest/bookkeeping/books/' + bookId + '/groups/' + number, {
      title: title
    }).pipe(takeUntil(this.closer$))
      .subscribe({
        next: (group) => {
          this.toastr.success("Group Saved", "Account Group " + number + " was saved successfully")
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not save account group")
          this.loadBook(bookId)
        }
      })
  }

  deleteGroup(bookId: number, groupNumber: number) {
    this.http.delete<any>('/rest/bookkeeping/books/' + bookId + '/groups/' + groupNumber)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.toastr.success("Group Deleted", "Account Group " + groupNumber + " was successfully deleted")
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not delete account group")
          this.loadBook(bookId)
        }
      })
  }

  saveAccount(bookId: number, accountId: string, title: string, description: string | undefined, type: AccountType) {
    this.http.put<AccountSummary>('/rest/bookkeeping/books/' + bookId + '/accounts/' + accountId, {
        title: title,
        description: description,
        accountType: type
      }
    ).pipe(takeUntil(this.closer$))
      .subscribe({
        next: (account) => {
          this.toastr.success("Account Saved", "Account " + accountId + " was saved successfully")
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not save account ")
          this.loadBook(bookId)
        }
      })
  }

  deleteAccount(bookId: number, accountId: String) {
    this.http.delete<any>('/rest/bookkeeping/books/' + bookId + '/accounts/' + accountId)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (_) => {
          this.toastr.success("Account Deleted", "Account " + accountId + " was successfully deleted")
          this.loadBook(bookId)
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not delete account")
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
        page: this.bookingPageState.pageNo
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
          this.toastr.success("Transaction Saved", "Transaction " + bookingId + " was successfully updated")
          this.reloadBookings()
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not edit transaction")
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
          this.toastr.success("Transaction Saved", "Transaction was successfully deleted")
          this.reloadBookings()
          result.next(1)
        },
        error: error => {
          this.toastr.error(error?.error?.message, "could not save transaction")
          this.reloadBookings()
          result.next(-1)
        }
      })
    return result
  }

  private formatDate(date: Date) {
    let d = new Date(date),
      month = '' + (d.getMonth() + 1),
      day = '' + d.getDate(),
      year = d.getFullYear();

    if (month.length < 2)
      month = '0' + month;
    if (day.length < 2)
      day = '0' + day;

    return [year, month, day].join('-');
  }

  private mapBookingRecord(booking: CreateBookingRecord): CreateBookingRecordDto {
    return {
      bookingDate: this.formatDate(booking.date),
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

  loadBookings(bookId: number, pageId: number) {
    this.bookingPageState.bookId = bookId
    this.bookingPageState.pageNo = pageId
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
          this.toastr.error(error?.error?.message, "could not delete account")
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
  id: string,
  title: string,
  description?: string,
  accountType: AccountType
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

export interface BookingRecord {
  id: number,
  date: Date,
  tag?: string,
  text: string,
  description?: string,
  amount: Big,
  credits: BookingMovement[],
  debits: BookingMovement[],
}

export interface BookingMovement {
  accountId: string,
  amount: Big,
}

export enum AccountType {
  ASSET = "ASSET",
  LIABILITY = "LIABILITY",
  EXPENSE = "EXPENSE",
  INCOME = "INCOME",
}

export class AccountTypeUtil {
  public static iconForType(type: AccountType): string {
    switch (type) {
      case AccountType.ASSET:
        return 'savings'
      case AccountType.LIABILITY:
        return 'credit_card'
      case AccountType.INCOME:
        return 'trending_up'
      case AccountType.EXPENSE:
        return 'trending_down'
    }
  }
}

export class MoneyUtil {
  public static formatForDisplay(amount: Big): string {
    let rawString = amount.toFixed(2)
    if (rawString.startsWith('0.')) {
      return '-.' + rawString.split('.')[1]
    }
    if (rawString.endsWith('.00')) {
      return rawString.split('.')[0] + '.--'
    } else {
      return rawString
    }
  }
}

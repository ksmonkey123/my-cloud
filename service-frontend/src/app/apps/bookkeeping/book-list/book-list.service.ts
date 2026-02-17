import {Injectable} from "@angular/core";
import {BaseRequestStateService} from "../../../common/base/base-request-state.service";
import {HttpClient} from "@angular/common/http";
import {map, Observable} from "rxjs";
import {BookSummary} from "../model/bookSummary";

@Injectable()
export class BookListService extends BaseRequestStateService<BookListRequestState, BookSummary[]> {

  constructor(private http: HttpClient) {
    super();
  }

  protected override getBusinessKey(): string | undefined {
    return "bookkeeping-book-list"
  }

  protected override getInitialRequest(): BookListRequestState | undefined {
    return {closed: false};
  }

  fetchBookList(closed: boolean): Observable<BookSummary[]> {
    return this.http.get<BookSummaryDto[]>('/rest/bookkeeping/books', {params: {'closed': closed}})
      .pipe(
        map(list => list.map(this.mapBookSummaryDto))
      );
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

}

export interface BookListRequestState {
  closed?: boolean
}

interface BookSummaryDto {
  id: number,
  title: string,
  description?: string,
  openingDate: string,
  closingDate: string,
  closed: boolean,
}


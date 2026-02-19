import {BaseRequestStateService} from "./base-request-state.service";
import {Observable} from "rxjs";

export abstract class BasePagingService<REQ, RSP> extends BaseRequestStateService<PagingRequest<REQ>, RSP> {

  public static readonly DEFAULT_PAGE_SIZE = 20;

  protected override getInitialRequest(): PagingRequest<REQ> | undefined {
    return {
      requestData: undefined,
      pageIndex: 0,
      pageSize: this.getPageSize()
    };
  }

  public getPageSize(): number {
    return BasePagingService.DEFAULT_PAGE_SIZE;
  }

  public setPagingRequestData(requestData: REQ) {
    this.requestState$.next({
      requestData: requestData,
      pageIndex: 0,
      pageSize: this.getPageSize()
    });
  }

  public resetPagingRequestData() {
    this.requestState$.next({
      requestData: undefined,
      pageIndex: 0,
      pageSize: this.getPageSize()
    });
  }

  public setPageIndex(pageIndex: number) {
    if (this.requestState$.value) {
      this.requestState$.next({
        ...this.requestState$.value,
        pageIndex: pageIndex
      });
    }
  }

  public getCurrentPagingRequestData(): REQ | undefined {
    return this.requestState$.value?.requestData;
  }

  public abstract getItems(pagingRequest: PagingRequest<REQ>): Observable<PagingResult<RSP | null>>

}

export interface PagingRequest<T> {
  pageIndex: number
  pageSize: number
  requestData: T | undefined
}

export interface PagingResult<T> {
  items: T[];
  totalItems: number
}

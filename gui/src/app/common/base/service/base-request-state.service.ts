import {BaseLocalDateChangeService} from "./base-local-data-change-service.service";
import {BehaviorSubject} from "rxjs";
import {StorageUtils} from "../utils/ui.utils";

export abstract class BaseRequestStateService<REQ, RSP> extends BaseLocalDateChangeService<RSP> {

  public readonly requestState$ = new BehaviorSubject<REQ | undefined>(this.getInitialRequest());

  private static readonly REQUEST_STORAGE_KEY_EXTENSION = '_REQ';

  protected getBusinessKey(): string | undefined {
    return undefined;
  }

  protected getInitialRequest(): REQ | undefined {
    return undefined;
  }

  constructor() {
    super();
    StorageUtils.handleStorage(this.requestState$, BaseRequestStateService.REQUEST_STORAGE_KEY_EXTENSION, this.getBusinessKey());
  }

  public patchRequestState(requestState: REQ) {
    this.requestState$.next({...this.requestState$.value, ...requestState});
  }

  public reloadRequestState() {
    const current = this.requestState$.value;
    if (current) {
      this.requestState$.next({...current});
    } else {
      this.requestState$.next(undefined);
    }
  }

}

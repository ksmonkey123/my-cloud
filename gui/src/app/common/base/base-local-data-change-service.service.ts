import {Subject} from 'rxjs';

export abstract class BaseLocalDateChangeService<BO> {

  public readonly localDataChanges$ = new Subject<BO[] | undefined>();

  public setLocalDataChanges(localDataChanges: BO[]) {
    this.localDataChanges$.next(localDataChanges.slice());
  }

}

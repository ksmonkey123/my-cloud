import {Subject} from 'rxjs';

export abstract class BaseLocalDataChangeService<BO> {

  public readonly localDataChanges$ = new Subject<ChangeSet<BO>>();

  public setLocalDataChanges(localDataChanges: ChangeSet<BO>) {
    this.localDataChanges$.next(
      {
        added: localDataChanges.added?.slice() || [],
        removed: localDataChanges.removed?.slice() || [],
      }
    );
  }

}

export interface ChangeSet<T> {
  added?: T[]
  removed?: T[]
}

export function patchState<T>(state: T[] | undefined, changeSet: ChangeSet<T>) {
  let remaining = (state || []).filter(item => !(changeSet.removed || []).includes(item))
  return [...remaining, ...(changeSet.added || [])]
}

export function mapChangeSet<T, S>(changeSet: ChangeSet<T>, mapper: (item: T) => S): ChangeSet<S> {
  return {
    added: changeSet.added?.map(mapper) || [],
    removed: changeSet.removed?.map(mapper) || [],
  }
}

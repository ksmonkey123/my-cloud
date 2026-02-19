import {BehaviorSubject, delay} from "rxjs";

export class StorageUtils {

  public static handleStorage<T>(stateObservable$: BehaviorSubject<T>, keyExtension: string, businessKey: string | undefined) {
    if (businessKey !== undefined) {
      const storageKey = businessKey + keyExtension;
      const valueRaw = localStorage.getItem(storageKey);
      if (valueRaw) {
        try {
          stateObservable$.next(JSON.parse(valueRaw));
        } catch {
          localStorage.removeItem(storageKey);
        }
      }
      stateObservable$.pipe(delay(1)).subscribe(value => {
        if (value) {
          localStorage.setItem(storageKey, JSON.stringify(value));
        } else {
          localStorage.removeItem(storageKey);
        }
      })
    }
  }

}

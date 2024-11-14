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

export class ComponentStateUtils {

  private static componentStateObservables = new Map<string, BehaviorSubject<any>>();

  public static registerComponentState<T>(keyExtension: string, businessKey: string, state$: BehaviorSubject<T>) {
    return this.componentStateObservables.set(businessKey + keyExtension, state$);
  }

  public static getComponentState<T>(keyExtension: string, businessKey: string): BehaviorSubject<T> | undefined {
    return this.componentStateObservables.get(businessKey + keyExtension);
  }

}

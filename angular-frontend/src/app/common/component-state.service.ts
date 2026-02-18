import {BehaviorSubject} from "rxjs";
import {StorageUtils} from "./storage.utils";
import {signal, Signal} from "@angular/core";
import {StateUtils} from "./state.utils";

export class ComponentStateService<T> {

  private static readonly COMPONENT_STATE_STORAGE_KEY_EXTENSION = '_COMP_STATE'
  private static channelBags = new Map<string, ChannelBag>;

  private businessKey: string;

  public readonly state$: BehaviorSubject<T | undefined>;
  public readonly state: Signal<T | undefined>;

  constructor(businessKey: string, useLocalStore: boolean = true) {
    this.businessKey = businessKey;

    const cachedBag = ComponentStateService.channelBags.get(businessKey);

    if (cachedBag) {
      // reuse existing bag
      this.state$ = cachedBag.subject$;
      this.state = cachedBag.signal;
    } else {
      this.state$ = new BehaviorSubject<T | undefined>(undefined);
      const writableSignal = signal<T | undefined>(undefined);
      this.state$.subscribe(value => writableSignal.set(value));
      this.state = writableSignal;

      ComponentStateService.channelBags.set(businessKey, {
        signal: this.state,
        subject$: this.state$
      });

      if (useLocalStore) {
        StorageUtils.handleStorage(this.state$, ComponentStateService.COMPONENT_STATE_STORAGE_KEY_EXTENSION, businessKey);
      }
    }
  }

  public getCurrentComponentState(): T | undefined {
    return this.state$.value;
  }

  public patchComponentState(patch: T) {
    StateUtils.handlePatchState(patch, this.state$);
  }

  public reloadComponentState() {
    if (this.state$.value) {
      this.state$.next({...this.state$.value});
    } else {
      this.state$.next(undefined);
    }
  }

}

interface ChannelBag {
  subject$: BehaviorSubject<any>,
  signal: Signal<any>
}

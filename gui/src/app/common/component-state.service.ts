import {BehaviorSubject} from "rxjs";
import {ComponentStateUtils, StorageUtils} from "./base/utils/ui.utils";

export class ComponentStateService<T> {

  private static readonly COMPONENT_STATE_STORAGE_KEY_EXTENSION = '_COMP_STATE'

  private businessKey: string;

  public readonly componentState$: BehaviorSubject<T | undefined>;

  constructor(businessKey: string, useLocalStore: boolean = true) {
    this.businessKey = businessKey;
    const cached$ = ComponentStateUtils.getComponentState<T | undefined>(ComponentStateService.COMPONENT_STATE_STORAGE_KEY_EXTENSION, businessKey);
    if (cached$) {
      this.componentState$ = cached$;
    } else {
      this.componentState$ = new BehaviorSubject<T | undefined>(undefined);
      ComponentStateUtils.registerComponentState(ComponentStateService.COMPONENT_STATE_STORAGE_KEY_EXTENSION, businessKey, this.componentState$);
      if (useLocalStore) {
        StorageUtils.handleStorage(this.componentState$, ComponentStateService.COMPONENT_STATE_STORAGE_KEY_EXTENSION, businessKey);
      }
    }
  }

  public getCurrentComponentState(): T | undefined {
    return this.componentState$.value;
  }

  public patchComponentState(patch: T) {
    this.componentState$.next({
      ...this.componentState$.value,
      ...patch
    });
  }

  public reloadComponentState() {
    if (this.componentState$.value) {
      this.componentState$.next({...this.componentState$.value});
    } else {
      this.componentState$.next(undefined);
    }
  }

}

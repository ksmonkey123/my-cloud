import {BehaviorSubject} from "rxjs";

export class StateUtils {

  private static isPrimitiveOrArray<C>(value: C): boolean {
    return ((typeof (value) === 'string') ||
      (typeof (value) === 'number') ||
      (typeof (value) === 'boolean') ||
      Array.isArray(value))
  }

  /**
   * patches the value into the subject
   *
   * @param value
   * @param behaviorSubject
   */
  public static handlePatchState<C>(value: C, behaviorSubject: BehaviorSubject<C>) {
    // eslint-disable-next-line  @typescript-eslint/no-explicit-any
    let patchData: any = value;
    const currentComponentData = behaviorSubject.value;
    let primitiveDataType = false;
    // if the value is undefined or null, let the current state value decide or return
    if ((value === null) || (value === undefined)) {
      if ((currentComponentData === null) || (currentComponentData === undefined)) {
        return;
      }
      primitiveDataType = StateUtils.isPrimitiveOrArray(currentComponentData);
    } else {
      primitiveDataType = StateUtils.isPrimitiveOrArray(value);
    }

    if (primitiveDataType) {
      if (value !== currentComponentData) {
        behaviorSubject.next(value);
      }
    } else {
      if (currentComponentData) {
        patchData = {};
        let property: keyof typeof value;
        for (property in value) {
          if (JSON.stringify(currentComponentData[property]) !== JSON.stringify(value[property])) {
            patchData[property] = value[property];
          }
        }
      }
      if (Object.keys(patchData).length < 1) {
        return;
      }
      behaviorSubject.next(
        {
          ...behaviorSubject.value,
          ...patchData,
        });
    }
  }
}

import {computed, inject, Injectable, OnDestroy, Signal, signal} from "@angular/core";
import {Subject, takeUntil} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {CanActivateFn} from "@angular/router";

@Injectable({providedIn: 'root'})
export class FeaturesService implements OnDestroy {

  private featureState = signal<FeatureState>({defaultState: false, features: {}})

  private closer$ = new Subject<void>()

  constructor(private http: HttpClient) {
  }

  /**
   * Fetches (or re-fetches) the feature flags.
   */
  fetch() {
    this.http.get<FeatureInfo>('rest/features')
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (features: FeatureInfo) => {
          const state: FeatureState = {defaultState: features.defaultState, features: {}}
          for (const feature of features.features) {
            state.features[feature.id] = feature.enabled
          }

          this.featureState.set(state)
        }
      })
  }

  private tests = new Map<string, Signal<boolean>>()

  test(id: string): Signal<boolean> {
    if (this.tests.has(id)) {
      return this.tests.get(id)!!
    }

    const result = computed(() => {
        let state = this.featureState()
        return state.features[id] ?? state.defaultState
      }
    )

    this.tests.set(id, result)
    return result
  }

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
  }

}

export interface Feature {
  id: string
  enabled: boolean
}

interface FeatureInfo {
  defaultState: boolean
  features: Feature[]
}

interface FeatureState {
  defaultState: boolean
  features: FeatureDict
}

interface FeatureDict {
  [id: string]: boolean
}

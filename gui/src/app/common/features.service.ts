import {Injectable, OnDestroy, signal} from "@angular/core";
import {Subject, takeUntil} from "rxjs";
import {HttpClient} from "@angular/common/http";

@Injectable({providedIn: 'root'})
export class FeaturesService implements OnDestroy {

  private featureState: FeatureState = {defaultState: false, features: {}}

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

          this.featureState = state
        }
      })
  }

  /**
   * tests if the given feature is enabled
   *
   * @param id the feature id
   */
  test(id: string): boolean {
    return this.featureState.features[id] ?? this.featureState.defaultState
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

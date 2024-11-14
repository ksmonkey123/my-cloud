import {AfterViewInit, Component, EventEmitter, OnDestroy, Output, signal} from "@angular/core";
import {interval, Observable, Subject, Subscription, take, takeUntil} from "rxjs";
import {ErrorUtils} from "../utils/error.utils";

@Component({template: ''})
export abstract class BaseDataComponent<T> implements AfterViewInit, OnDestroy {

  protected readonly processingState = signal(ProcessingState.INITIAL)
  protected readonly data = signal<T | undefined>(undefined)
  protected readonly errorMessage = signal<string[] | null>(null)

  private currentSubscription: Subscription | undefined;
  protected unsubscribe$ = new Subject<void>()

  @Output()
  processingStateChange = new EventEmitter<ProcessingState>();

  ngAfterViewInit() {
  }

  ngOnDestroy() {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  protected handleReloadAfterError() {
  }

  private startProcessing() {
    this.processingState.set(ProcessingState.PROCESSING);
    interval(1).pipe(take(1)).subscribe(() => {
      this.processingStateChange.emit(ProcessingState.PROCESSING);
      this.onAfterStartProcessing();
    });
  }

  protected onAfterStartProcessing() {
  }

  private completedProcessingFilled() {
    this.processingState.set(ProcessingState.COMPLETED_FILLED);
    interval(1).pipe(take(1)).subscribe(() => {
      this.processingStateChange.emit(ProcessingState.COMPLETED_FILLED);
      this.onAfterCompletedProcessingFilled();
    });
  }

  protected onAfterCompletedProcessingFilled() {
  }

  private completedProcessingEmpty() {
    this.processingState.set(ProcessingState.COMPLETED_EMPTY);
    interval(1).pipe(take(1)).subscribe(() => {
      this.processingStateChange.emit(ProcessingState.COMPLETED_EMPTY);
      this.onAfterCompletedProcessingEmpty();
    });
  }

  protected onAfterCompletedProcessingEmpty() {
  }

  private error(err: any) {
    this.errorMessage.set(ErrorUtils.readHttpError(err));
    this.processingState.set(ProcessingState.ERROR);
    interval(1).pipe(take(1)).subscribe(() => {
      this.processingStateChange.emit(ProcessingState.ERROR);
      this.onAfterError();
    })
  }

  protected onAfterError() {
  }

  protected onBeforeSetData(data: T) {
  }

  protected loadData(data$: Observable<T>) {
    if (this.currentSubscription) {
      this.currentSubscription.unsubscribe();
    }
    this.startProcessing();
    this.currentSubscription = data$.pipe(takeUntil(this.unsubscribe$)).subscribe({
      next: value => {
        this.onBeforeSetData(value);
        this.data.set(value);
        if (value) {
          this.completedProcessingFilled();
        } else {
          this.completedProcessingEmpty();
        }
      },
      error: err => {
        this.error(err);
      }
    })
  }

}

export enum ProcessingState {
  INITIAL = 'INITIAL',
  PROCESSING = 'PROCESSING',
  COMPLETED_FILLED = 'COMPLETED_FILLED',
  COMPLETED_EMPTY = 'COMPLETED_EMPTY',
  ERROR = 'ERROR'
}

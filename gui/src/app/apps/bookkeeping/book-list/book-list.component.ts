import {Component} from '@angular/core';
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatChip} from "@angular/material/chips";
import {BaseDataComponent, ProcessingState} from "../../../common/base/base-data.component";
import {ComponentStateService} from "../../../common/component-state.service";
import {NgxSkeletonLoaderModule} from "ngx-skeleton-loader";
import {TranslocoPipe} from "@jsverse/transloco";
import {MatSlideToggle, MatSlideToggleChange} from "@angular/material/slide-toggle";
import {BookSummary} from "../model/bookSummary";
import {BookListService} from "./book-list.service";
import {takeUntil} from "rxjs";

@Component({
  selector: 'app-bookkeeping',
  providers: [],
  imports: [
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatIcon,
    RouterLink,
    FormsModule,
    ReactiveFormsModule,
    MatChip,
    NgxSkeletonLoaderModule,
    TranslocoPipe,
    MatSlideToggle,
  ],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent extends BaseDataComponent<BookSummary[]> {

  public componentStateService = new ComponentStateService<BookListComponentState>("bookkeeping-book-list")

  constructor(public service: BookListService) {
    super()
  }

  protected override onBeforeSetData(data: BookSummary[]) {
    this.componentStateService.patchComponentState({numberOfBooks: data.length})
  }

  includeClosed = false;

  onToggle(event: MatSlideToggleChange) {
    this.service.patchRequestState({closed: event.checked})
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();

    this.service.requestState$.pipe(takeUntil(this.unsubscribe$)).subscribe(state => {
        this.includeClosed = state?.closed ?? false;
        this.loadData(this.service.fetchBookList(state?.closed ?? false))
      }
    )
  }

  protected readonly ProcessingState = ProcessingState;
}

interface BookListComponentState {
  numberOfBooks?: number;
}

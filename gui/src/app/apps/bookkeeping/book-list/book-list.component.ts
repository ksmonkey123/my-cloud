import {Component} from '@angular/core';
import {BookkeepingService, BookSummary} from "../bookkeeping.service";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatChip} from "@angular/material/chips";
import {BaseDataComponent, ProcessingState} from "../../../common/base/base-data.component";
import {ComponentStateService} from "../../../common/component-state.service";
import {NgxSkeletonLoaderModule} from "ngx-skeleton-loader";

@Component({
  selector: 'app-bookkeeping',
  standalone: true,
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
  ],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent extends BaseDataComponent<BookSummary[]> {

  public componentStateService = new ComponentStateService<BookListComponentState>("bookkeeping-book-list")

  constructor(public service: BookkeepingService) {
    super()
  }

  protected override onBeforeSetData(data: BookSummary[]) {
    this.componentStateService.patchComponentState({numberOfBooks: data.length})
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();
    this.loadData(this.service.fetchBookList())
  }

  protected readonly ProcessingState = ProcessingState;
}

interface BookListComponentState {
  numberOfBooks?: number;
}

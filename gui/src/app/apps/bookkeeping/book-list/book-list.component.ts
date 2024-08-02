import {Component} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {BookkeepingService, BookSummary} from "../bookkeeping.service";
import {AsyncPipe} from "@angular/common";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {MatDialogContent} from "@angular/material/dialog";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {
  MatDatepickerToggle,
  MatDatepickerToggleIcon,
  MatDateRangeInput,
  MatDateRangePicker,
  MatEndDate,
  MatStartDate
} from "@angular/material/datepicker";
import {MatFormField, MatSuffix} from "@angular/material/form-field";
import {provideNativeDateAdapter} from "@angular/material/core";
import {MatChip} from "@angular/material/chips";

@Component({
  selector: 'app-bookkeeping',
  standalone: true,
  providers: [],
  imports: [
    AsyncPipe,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatIcon,
    RouterLink,
    FormsModule,
    MatDateRangeInput,
    MatDateRangePicker,
    MatDatepickerToggle,
    MatDatepickerToggleIcon,
    MatEndDate,
    MatStartDate,
    ReactiveFormsModule,
    MatSuffix,
    MatDialogContent,
    MatFormField,
    MatChip,
  ],
  templateUrl: './book-list.component.html',
  styleUrl: './book-list.component.scss'
})
export class BookListComponent {

  public list$: BehaviorSubject<BookSummary[]>

  constructor(service: BookkeepingService) {
    service.loadBooks()
    this.list$ = service.bookList$
  }

}

import {Component} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {BookkeepingService, BookSummary} from "./bookkeeping.service";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";

@Component({
  selector: 'app-bookkeeping',
  standalone: true,
  imports: [
    AsyncPipe,
    MatCard,
    MatCardContent,
    MatCardHeader,
    MatCardTitle,
    MatIcon,
    NgIf,
    RouterLink,
    NgForOf
  ],
  templateUrl: './bookkeeping.component.html',
  styleUrl: './bookkeeping.component.scss'
})
export class BookkeepingComponent {

  public list$: BehaviorSubject<BookSummary[]>

  constructor(private service: BookkeepingService) {
    service.loadBooks()
    this.list$ = service.bookList$
  }

  protected readonly BookkeepingService = BookkeepingService;

  addBook() {

  }
}

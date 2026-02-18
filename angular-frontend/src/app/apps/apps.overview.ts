import {Routes} from "@angular/router";
import {ShortenerComponent} from "./shortener/shortener.component";
import {ShortenerService} from "./shortener/shortener.service";
import {BookListComponent} from "./bookkeeping/book-list/book-list.component";
import {BookkeepingService} from "./bookkeeping/bookkeeping.service";
import {BookDetailsComponent} from "./bookkeeping/book-details/book-details.component";
import {BookListService} from "./bookkeeping/book-list/book-list.service";

export const apps: AppsInformation = {
  routes: [
    {path: 'shortener', component: ShortenerComponent, providers: [ShortenerService]},
    {
      path: 'bookkeeping', providers: [BookkeepingService, BookListService], children: [
        {path: '', component: BookListComponent},
        {path: ':book_id', component: BookDetailsComponent},
      ],
    }
  ],
  cards: [
    {path: '/shortener', titleKey: 'shortener.title', auth: 'shortener', feature: 'shortener:frontend', icon: 'share'},
    {
      path: '/bookkeeping',
      titleKey: 'bookkeeping.title',
      auth: 'bookkeeping',
      feature: 'bookkeeping:frontend',
      icon: 'account_balance'
    },
  ]
}

export interface AppsInformation {
  routes: Routes,
  cards: AppCard[]
}

export interface AppCard {
  path: string
  titleKey: string
  auth: string
  feature: string
  icon?: string
}

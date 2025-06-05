import {Routes} from "@angular/router";
import {ShortenerComponent} from "./shortener/shortener.component";
import {ShortenerService} from "./shortener/shortener.service";
import {CanaryComponent} from "./canary/canary.component";
import {BookListComponent} from "./bookkeeping/book-list/book-list.component";
import {BookkeepingService} from "./bookkeeping/bookkeeping.service";
import {BookDetailsComponent} from "./bookkeeping/book-details/book-details.component";
import {BookListService} from "./bookkeeping/book-list/book-list.service";
import {DockerService} from "./canary/docker/docker.service";
import {DockerDetailsComponent} from "./canary/docker-details/docker-details.component";

export const apps: AppsInformation = {
  routes: [
    {path: 'shortener', component: ShortenerComponent, providers: [ShortenerService]},
    {
      path: 'canary', providers: [DockerService], children: [
        {path: '', component: CanaryComponent},
        {path: '/docker/:namespace/:repository/:tag', component: DockerDetailsComponent},
      ]
    },
    {
      path: 'bookkeeping', providers: [BookkeepingService, BookListService], children: [
        {path: '', component: BookListComponent},
        {path: ':book_id', component: BookDetailsComponent},
      ]
    }
  ],
  cards: [
    {path: '/shortener', titleKey: 'shortener.title', auth: 'shortener', icon: 'share'},
    {path: '/canary', titleKey: 'canary.title', auth: 'canary', icon: 'network_check'},
    {path: '/bookkeeping', titleKey: 'bookkeeping.title', auth: 'bookkeeping', icon: 'account_balance'},
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
  icon?: string
}

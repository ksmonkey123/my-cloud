import {Routes} from "@angular/router";
import {ShortenerComponent} from "./shortener/shortener.component";
import {ShortenerService} from "./shortener/shortener.service";
import {CanaryComponent} from "./canary/canary.component";
import {BookkeepingComponent} from "./bookkeeping/bookkeeping.component";
import {BookkeepingService} from "./bookkeeping/bookkeeping.service";

export const apps: AppsInformation = {
  routes: [
    {path: 'shortener', component: ShortenerComponent, providers: [ShortenerService]},
    {path: 'canary', component: CanaryComponent},
    {
      path: 'bookkeeping', providers:[BookkeepingService], children: [
        {path: '', component: BookkeepingComponent}
      ]
    }
  ],
  cards: [
    {path: '/shortener', title: 'URL-Shortener', auth: 'shortener', icon: 'share'},
    {path: '/canary', title: 'Website Canary Scan', auth: 'canary', icon: 'network_check'},
    {path: '/bookkeeping', title: 'Bookkeeping', auth: 'bookkeeping', icon: 'account_balance'},
  ]
}

export interface AppsInformation {
  routes: Routes,
  cards: AppCard[]
}

export interface AppCard {
  path: string
  title: string
  auth: string
  icon?: string
}

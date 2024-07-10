import {Routes} from "@angular/router";
import {ShortenerComponent} from "./shortener/shortener.component";
import {ShortenerService} from "./shortener/shortener.service";
import {CanaryComponent} from "./canary/canary.component";

export const apps: AppsInformation = {
  routes: [
    {path: 'shortener', component: ShortenerComponent, providers: [ShortenerService]},
    {path: 'canary', component: CanaryComponent}
  ],
  cards: [
    {path: '/shortener', title: 'URL-Shortener', auth: 'shortener', icon: 'share'},
    {path: '/canary', title: 'Website Canary Scan', auth: 'canary', icon: 'network_check'},
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

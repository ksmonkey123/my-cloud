import {Routes} from "@angular/router";
import {HomeComponent} from "./home/home.component";
import {ShortenerComponent} from "./shortener/shortener.component";
import {ShortenerService} from "./shortener/shortener.service";
import {CanaryComponent} from "./canary/canary.component";

export const page_routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'shortener', component: ShortenerComponent, providers: [ShortenerService]},
  {path: 'canary', component: CanaryComponent}
]

export const primary_pages: PageOverview[] = [
  {path: '/shortener', title: 'URL-Shortener', auth: 'shortener', icon: 'share'},
  {path: '/canary', title: 'Website Canary Scan', auth: 'canary', icon: 'network_check'},
]

export interface PageOverview {
  path: string
  title: string
  auth: string
  icon?: string
}

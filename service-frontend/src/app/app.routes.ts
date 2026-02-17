import {Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {settings} from "./settings/settings.routes";
import {HomeComponent} from "./home/home.component";
import {apps} from "./apps/apps.overview";

export const routes: Routes = [
  ...apps.routes,
  ...settings.routes,
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: '**', redirectTo: ''},
];

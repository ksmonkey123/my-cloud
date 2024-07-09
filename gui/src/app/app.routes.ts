import {Routes} from '@angular/router';
import {LoginComponent} from "./login/login.component";
import {settings_routes} from "./settings/settings.routes";
import {app_routes} from "./apps/apps.overview";
import {HomeComponent} from "./home/home.component";

export const routes: Routes = [
  ...app_routes,
  ...settings_routes,
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: '**', redirectTo: ''},
];

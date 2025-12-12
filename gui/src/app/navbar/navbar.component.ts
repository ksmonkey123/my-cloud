import {Component} from '@angular/core';
import {AuthService} from "../common/auth.service";
import {Router, RouterLink, RouterLinkActive} from "@angular/router";

import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatMenu, MatMenuItem, MatMenuTrigger} from "@angular/material/menu";
import {MatToolbar} from "@angular/material/toolbar";
import {settings} from "../settings/settings.routes";
import {TranslocoPipe} from "@jsverse/transloco";

@Component({
  selector: 'app-navbar',
  imports: [
    MatButton,
    MatIcon,
    MatIconButton,
    MatMenu,
    MatMenuItem,
    MatToolbar,
    RouterLink,
    RouterLinkActive,
    MatMenuTrigger,
    TranslocoPipe
],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.scss'
})
export class NavbarComponent {

  constructor(public auth: AuthService, private router: Router) {
  }

  onLogout() {
    this.auth.fullLogout(() => {
      void this.router.navigate(['/login'])
    })
  }

  protected readonly settingsOptions = settings.options;
}

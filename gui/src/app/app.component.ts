import {AfterViewInit, Component} from '@angular/core';
import {EventType, NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {MatIconRegistry} from "@angular/material/icon";
import {AuthService} from "./common/auth.service";
import {NavbarComponent} from "./navbar/navbar.component";

@Component({
    selector: 'app-root',
    imports: [NavbarComponent, RouterOutlet],
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent implements AfterViewInit {

  constructor(public auth: AuthService, private router: Router, iconRegistry: MatIconRegistry) {
    iconRegistry.setDefaultFontSetClass('material-symbols-outlined')
  }

  ngAfterViewInit() {
    if (!this.router.url.startsWith('/login')) {
      this.auth.fetch()
    }

    // refresh auth info on every navigation event (and implicitly redirect to login page on bad auth)
    this.router.events.subscribe((e) => {
      if (e.type == EventType.NavigationEnd && !(e as NavigationEnd).url.startsWith('/login'))
        this.auth.fetch()
    })
  }

}

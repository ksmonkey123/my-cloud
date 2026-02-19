import {AfterViewInit, Component} from '@angular/core';
import {EventType, NavigationEnd, Router, RouterOutlet} from '@angular/router';
import {MatIconRegistry} from "@angular/material/icon";
import {AuthService} from "./common/auth.service";
import {NavbarComponent} from "./navbar/navbar.component";
import {FeaturesService} from "./common/features.service";

@Component({
  selector: 'app-root',
  imports: [NavbarComponent, RouterOutlet],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements AfterViewInit {

  constructor(public auth: AuthService, public features: FeaturesService, private router: Router, iconRegistry: MatIconRegistry) {
    iconRegistry.setDefaultFontSetClass('material-symbols-outlined')
  }

  ngAfterViewInit() {
    // refresh auth info on every navigation event (and implicitly redirect to login page on bad auth)
    this.router.events.subscribe((e) => {
      if (e.type == EventType.NavigationEnd && !(e as NavigationEnd).url.startsWith('/login')) {
        this.auth.fetch()
        this.features.fetch()
      }
    })
  }

}

import {Component, effect} from '@angular/core';
import {AuthService} from "../common/auth.service";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {AppCard, apps} from "../apps/apps.overview";
import {ComponentStateService} from "../common/component-state.service";
import {NgxSkeletonLoaderModule} from "ngx-skeleton-loader";
import {TranslocoPipe} from "@jsverse/transloco";
import {FeaturesService} from "../common/features.service";

@Component({
  selector: 'app-home',
  imports: [
    MatCard,
    MatCardTitle,
    MatCardHeader,
    MatCardContent,
    MatIcon,
    RouterLink,
    NgxSkeletonLoaderModule,
    TranslocoPipe
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  public readonly componentStateService = new ComponentStateService<HomeComponentState>("home");

  public cards: AppCard[] | undefined;

  constructor(public auth: AuthService, public features: FeaturesService) {
    effect(() => {
      const newAuth = auth.authInfo()
      if (newAuth) {
        this.cards = apps.cards.filter(card => {
          return (newAuth?.authorities ?? []).includes(card.auth) && features.test(card.feature)()
        });
        this.componentStateService.patchComponentState({numberOfCards: this.cards.length});
      }
    });
  }
}

interface HomeComponentState {
  numberOfCards?: number
}

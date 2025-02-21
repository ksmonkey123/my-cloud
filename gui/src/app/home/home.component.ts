import {Component, effect} from '@angular/core';
import {AuthService} from "../common/auth.service";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {AppCard, apps} from "../apps/apps.overview";
import {ComponentStateService} from "../common/component-state.service";
import {NgxSkeletonLoaderModule} from "ngx-skeleton-loader";

@Component({
    selector: 'app-home',
    imports: [
        MatCard,
        MatCardTitle,
        MatCardHeader,
        MatCardContent,
        MatIcon,
        RouterLink,
        NgxSkeletonLoaderModule
    ],
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {

  public readonly componentStateService = new ComponentStateService<HomeComponentState>("home");

  public cards: AppCard[] | undefined;

  constructor(public auth: AuthService) {
    effect(() => {
      const newAuth = auth.authInfo()
      if (newAuth) {
        this.cards = apps.cards.filter(card => {
          return (newAuth?.roles ?? []).includes(card.auth)
        });
        this.componentStateService.patchComponentState({numberOfCards: this.cards.length});
      }
    });
  }
}

interface HomeComponentState {
  numberOfCards?: number
}

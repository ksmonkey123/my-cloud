import {Component} from '@angular/core';
import {AuthService} from "../common/auth.service";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";
import {apps} from "../apps/apps.overview";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    NgForOf,
    MatCard,
    MatCardTitle,
    MatCardHeader,
    MatCardContent,
    MatIcon,
    NgIf,
    AsyncPipe,
    RouterLink
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

  constructor(public auth: AuthService) {
  }

  protected readonly appCards = apps.cards;
}

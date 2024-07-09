import {Component, OnInit} from '@angular/core';
import {AuthService} from "../common/auth.service";
import {primary_pages} from "../apps/apps.overview";
import {AsyncPipe, NgForOf, NgIf} from "@angular/common";
import {MatCard, MatCardContent, MatCardHeader, MatCardTitle} from "@angular/material/card";
import {MatIcon} from "@angular/material/icon";
import {RouterLink} from "@angular/router";

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
export class HomeComponent implements OnInit {

  public navItems = primary_pages

  constructor(public auth: AuthService) {
  }

  ngOnInit() {
    console.log("hi")
  }


  protected readonly primary_pages = primary_pages;
}

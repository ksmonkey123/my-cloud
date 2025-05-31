import {Component, OnInit} from '@angular/core';
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatTab, MatTabGroup} from "@angular/material/tabs";
import {FormControl} from "@angular/forms";
import {ActivatedRoute, Params, Router} from "@angular/router";
import {TranslocoDirective} from "@jsverse/transloco";
import {DockerComponent} from "./docker/docker.component";
import {WebcanaryComponent} from "./webcanary/webcanary.component";

@Component({
  selector: 'app-canary',
  imports: [
    MatCard,
    MatCardContent,
    MatTab,
    MatTabGroup,
    TranslocoDirective,
    DockerComponent,
    WebcanaryComponent
  ],
  templateUrl: './canary.component.html',
  styleUrl: './canary.component.scss'
})
export class CanaryComponent implements OnInit {

  selectedTabIndex = new FormControl(0)

  constructor(private route: ActivatedRoute,
              private router: Router,
  ) {
  }

  ngOnInit() {
    this.route.queryParams.subscribe((params: Params) => {
      let tabName = params['tab']
      if (tabName) {
        this.onTabChange(tabNameToIndex(tabName))
      }
    })
  }

  onTabChange(index: number) {
    this.selectedTabIndex.setValue(index)
    void this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {tab: tabIndexToName(index)},
    })
  }

}

function tabNameToIndex(name: string): number {
  switch (name) {
    case 'transactions':
      return 1
    default:
      return 0
  }
}

function tabIndexToName(index: number): string | undefined {
  switch (index) {
    case 0:
      return 'accounts'
    case 1:
      return 'transactions'
    default:
      return undefined
  }
}

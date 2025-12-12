import {Component} from '@angular/core';
import {ApiKey, ApiKeyManagementService} from "./api-key-management.service";
import {BaseDataComponent} from "../../common/base/base-data.component";
import {TranslocoDirective} from "@jsverse/transloco";
import {MatAccordion, MatExpansionPanel, MatExpansionPanelHeader} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-api-key-management',
  imports: [
    TranslocoDirective,
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatIcon
  ],
  providers: [ApiKeyManagementService],
  templateUrl: './api-key-management.component.html',
  styleUrl: './api-key-management.component.scss'
})
export class ApiKeyManagementComponent extends BaseDataComponent<ApiKey[]> {

  constructor(
    public service: ApiKeyManagementService,
  ) {
    super();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();

    this.loadData(this.service.list())
  }

}

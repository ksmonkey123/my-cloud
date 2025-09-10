import {Component} from '@angular/core';
import {ApiKey, ApiKeyManagementService} from "./api-key-management.service";
import {BaseDataComponent} from "../../common/base/base-data.component";

@Component({
  selector: 'app-api-key-management',
  imports: [],
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

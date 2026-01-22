import { Component } from '@angular/core';
import {BaseDataComponent} from "../../../common/base/base-data.component";
import {ApiKey, ApiKeyManagementService} from "../api-key-management.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef, MatHeaderCell,
  MatHeaderCellDef, MatHeaderRow,
  MatHeaderRowDef, MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatChip} from "@angular/material/chips";

@Component({
  selector: 'app-api-key-list',
  imports: [
    MatTable,
    MatHeaderCellDef,
    MatCellDef,
    MatHeaderRowDef,
    MatRowDef,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderRow,
    MatRow,
    MatIconButton,
    MatIcon,
    MatChip
  ],
  templateUrl: './api-key-list.component.html',
  styleUrl: './api-key-list.component.scss',
})
export class ApiKeyListComponent extends BaseDataComponent<ApiKey[]> {

  expandedKey: ApiKey | null = null;

  constructor(private service: ApiKeyManagementService) {
    super();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();
    this.refresh();
  }

  private refresh() {
    this.loadData(this.service.list());
  }

  isExpanded(key: ApiKey) {
    return this.expandedKey === key;
  }

  toggle(key: ApiKey) {
    this.expandedKey = this.isExpanded(key) ? null : key;
  }

}

import {Component, ViewChild} from '@angular/core';
import {BaseDataComponent} from "../../../common/base/base-data.component";
import {ApiKey, ApiKeyManagementService} from "../api-key-management.service";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatButton, MatIconButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {MatChip} from "@angular/material/chips";
import {SimpleModalService} from "../../../common/simple-modal/simple-modal.service";
import {ToastrService} from "ngx-toastr";
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";

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
    MatChip,
    MatButton,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle
  ],
  templateUrl: './api-key-list.component.html',
  styleUrl: './api-key-list.component.scss',
})
export class ApiKeyListComponent extends BaseDataComponent<ApiKey[]> {

  expandedKeyNames: string[] = [];

  constructor(private service: ApiKeyManagementService, private modal: SimpleModalService, private toast: ToastrService) {
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
    return this.expandedKeyNames.includes(key.name);
  }

  toggle(key: ApiKey) {
    if (!this.isExpanded(key)) {
      this.expandedKeyNames.push(key.name);
    } else {
      this.expandedKeyNames = this.expandedKeyNames.filter(k => k !== key.name);
    }
  }

  delete(key: ApiKey) {
    this.modal.confirm("delete key", "are you sure, you want to delete this API key?")
      .subscribe(result => {
          if (result) {
            this.service.delete(key.name)
              .subscribe({
                next: () => this.refresh(),
                error: error => {
                  this.refresh()
                  this.toast.error(error?.error?.message, "error deleting key")
                }
              })
          }
        }
      )
  }

  @ViewChild("noclose")
  alwaysOpenPanel !: MatExpansionPanel

  alwaysOpen() {
    this.alwaysOpenPanel.open();
  }

}

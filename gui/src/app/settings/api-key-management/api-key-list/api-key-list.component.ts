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
import {MatChip, MatChipSet} from "@angular/material/chips";
import {SimpleModalService} from "../../../common/simple-modal/simple-modal.service";
import {ToastrService} from "ngx-toastr";
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {of, takeUntil} from "rxjs";
import {translate, TranslocoPipe} from "@jsverse/transloco";

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
    MatExpansionPanelTitle,
    MatChipSet,
    TranslocoPipe
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

    this.service.localDataChanges$
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(newKeys => {
        if (newKeys) {
          this.loadData(of(newKeys.concat(this.data() || [])));
          for (let key of newKeys) {
            this.expandedKeyNames.push(key.name);
          }
        }
      });
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
    this.modal.confirm(
      translate("settings.api-keys.modal.delete.title"),
      translate("settings.api-keys.modal.delete.message", {name: key.name}))
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

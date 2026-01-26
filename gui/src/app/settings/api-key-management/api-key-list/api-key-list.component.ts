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
import {MatFormField, MatInput, MatLabel, MatSuffix} from "@angular/material/input";
import {MatSnackBar} from "@angular/material/snack-bar";
import {Clipboard} from "@angular/cdk/clipboard";
import {patchState} from "../../../common/base/base-local-data-change-service.service";
import {DatePipe} from "@angular/common";

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
    TranslocoPipe,
    MatFormField,
    MatLabel,
    MatInput,
    MatSuffix,
    DatePipe
  ],
  templateUrl: './api-key-list.component.html',
  styleUrl: './api-key-list.component.scss',
})
export class ApiKeyListComponent extends BaseDataComponent<ApiKey[]> {

  expandedKeyNames: string[] = [];

  constructor(
    private service: ApiKeyManagementService,
    private modal: SimpleModalService,
    private toast: ToastrService,
    private clipboard: Clipboard,
    private snackbar: MatSnackBar,
  ) {
    super();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();
    this.loadData(this.service.list());

    this.service.localDataChanges$
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(changes => {
        if (changes) {
          this.loadData(of(
            patchState(this.data(), changes).sort((a, b) => b.createdAt.getTime() - a.createdAt.getTime())
          ))
          for (let key of changes.added || []) {
            this.expandedKeyNames.push(key.name);
          }
        }
      });
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
                next: () => {
                  this.toast.success(translate("settings.api-keys.modal.delete.success"))
                  this.service.setLocalDataChanges({removed: [key]})
                },
                error: error => {
                  this.toast.error(error?.error?.message, translate("settings.api-keys.modal.delete.error"))
                }
              })
          }
        }
      )
  }

  copyToClipboard(token: string) {
    this.clipboard.copy(token)
    this.snackbar.open(translate('settings.api-keys.info.copy-success'), 'OK', {duration: 2000})
  }

  @ViewChild("noclose")
  alwaysOpenPanel !: MatExpansionPanel

  alwaysOpen() {
    this.alwaysOpenPanel.open();
  }
}

import {Component} from '@angular/core';
import {BaseDataComponent} from "../../common/base/base-data.component";
import {Feature} from "../../common/features.service";
import {FeaturesManagementService} from "./features-management.service";
import {TranslocoPipe, TranslocoService} from "@jsverse/transloco";
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
import {MatCard, MatCardContent} from "@angular/material/card";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {ToastrService} from "ngx-toastr";
import {MatIcon} from "@angular/material/icon";
import {MatMiniFabButton} from "@angular/material/button";
import {SimpleModalService} from "../../common/simple-modal/simple-modal.service";
import {MatDialog, MatDialogRef} from "@angular/material/dialog";
import {FeatureCreatePopup} from "./feature-create-popup/feature-create-popup.dialog";

@Component({
  selector: 'app-features-management',
  imports: [
    TranslocoPipe,
    MatTable,
    MatCardContent,
    MatCard,
    MatColumnDef,
    MatHeaderCell,
    MatCell,
    MatHeaderCellDef,
    MatCellDef,
    MatSlideToggle,
    MatRow,
    MatHeaderRow,
    MatHeaderRowDef,
    MatRowDef,
    MatIcon,
    MatMiniFabButton
  ],
  templateUrl: './features-management.component.html',
  styleUrl: './features-management.component.scss'
})
export class FeaturesManagementComponent extends BaseDataComponent<Feature[]> {

  displayedColumns = ['id', 'enabled', 'delete']

  constructor(private service: FeaturesManagementService,
              private toastr: ToastrService,
              private transloco: TranslocoService,
              private modal: SimpleModalService,
              private dialog: MatDialog,
  ) {
    super();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();
    this.refresh()
  }

  private refresh() {
    this.loadData(this.service.fetchList())
  }

  toggleFeature(feature: Feature) {
    this.service.updateFeature({...feature, enabled: !feature.enabled})
      .subscribe({
        next: () => this.refresh(),
        error: error => {
          this.toastr.error(error?.error?.message, this.transloco.translate("settings.feature.error.toggle", {id: feature.id}))
        }
      })
  }

  deleteFeature(feature: Feature) {
    console.log("delete feature " + feature.id)
    this.modal.confirm(
      this.transloco.translate("settings.feature.delete.title"),
      this.transloco.translate("settings.feature.delete.text", {id: feature.id}))
      .subscribe(confirm => {
        if (confirm) {
          this.service.deleteFeature(feature.id)
            .subscribe({
              next: () => this.refresh(),
              error: error => {
                this.toastr.error(error?.error?.message, this.transloco.translate("settings.feature.error.delete", {id: feature.id}))
              }
            })
        }
      })
  }

  openCreateDialog() {
    const dialogRef: MatDialogRef<FeatureCreatePopup, Feature> = this.dialog.open(FeatureCreatePopup)
    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        this.service.updateFeature(result)
          .subscribe({
            next: () => this.refresh(),
            error: error => {
              this.toastr.error(error?.error?.message, this.transloco.translate("settings.feature.error.create", {id: result.id}))
            }
          })
      }
    })
  }

}

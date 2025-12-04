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
    MatRowDef
  ],
  templateUrl: './features-management.component.html',
  styleUrl: './features-management.component.scss'
})
export class FeaturesManagementComponent extends BaseDataComponent<Feature[]> {

  displayedColumns = ['id', 'enabled']

  constructor(private service: FeaturesManagementService,
              private toastr: ToastrService,
              private translation: TranslocoService,
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
    console.log("toggle " + feature.id)
    this.service.updateFeatureState(feature.id, !feature.enabled)
      .subscribe({
        next: () => this.refresh(),
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("settings.feature.error.toggle", {id: feature.id}))
        }
      })
  }

}

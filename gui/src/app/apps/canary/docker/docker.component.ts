import {Component} from '@angular/core';
import {DockerImageSummary, DockerService} from "./docker.service";
import {BaseDataComponent} from "../../../common/base/base-data.component";
import {TranslocoPipe} from "@jsverse/transloco";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell, MatHeaderCellDef,
  MatHeaderRow, MatHeaderRowDef,
  MatRow, MatRowDef,
  MatTable
} from "@angular/material/table";
import {MatChip, MatChipSet} from "@angular/material/chips";

@Component({
  selector: 'app-canary-docker',
  imports: [
    TranslocoPipe,
    MatButton,
    MatIcon,
    MatTable,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatRow,
    MatHeaderRowDef,
    MatHeaderCellDef,
    MatRowDef,
    MatChipSet,
    MatChip,

  ],
  templateUrl: './docker.component.html',
  styleUrl: './docker.component.scss'
})
export class DockerComponent extends BaseDataComponent<DockerImageSummary[]> {

  constructor(private service: DockerService) {
    super();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();

    this.loadData(this.service.loadList())
  }

  displayedColumns = ['identifier', 'tag', 'tags', 'update']

  openNewEntryDialog() {
    console.log("open dialog")
  }

  onSelectItem(item: DockerImageSummary) {
    console.log("picking: " + item)
  }

}

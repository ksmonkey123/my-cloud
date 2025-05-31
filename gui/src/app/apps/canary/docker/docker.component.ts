import {Component} from '@angular/core';
import {DockerImageSummary, DockerService} from "./docker.service";
import {BaseDataComponent} from "../../../common/base/base-data.component";

@Component({
  selector: 'app-canary-docker',
  imports: [],
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

}

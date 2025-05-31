import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";

@Injectable()
export class DockerService {

  constructor(private http: HttpClient) {
  }

  loadList() {
    return this.http.get<DockerImageSummary[]>('/rest/canary/docker/images')
  }

}

export interface DockerImageSummary {
  identifier: string,
  tag: string,
  state?: DockerImageState
}

export interface DockerImageState {
  digest: string,
  tags: string[],
  recordedAt: string,
}

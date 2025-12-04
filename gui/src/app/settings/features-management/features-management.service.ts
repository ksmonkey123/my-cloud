import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Feature} from "../../common/features.service";
import {map} from "rxjs";

@Injectable()
export class FeaturesManagementService {

  constructor(private http: HttpClient) {
  }

  fetchList() {
    return this.http.get<{ features: Feature[] }>("/rest/features")
      .pipe(map((response) => response.features))
  }

  updateFeatureState(id: string, enabled: boolean) {
    return this.http.put(`/rest/features/${id}`, {enabled: enabled})
  }

}

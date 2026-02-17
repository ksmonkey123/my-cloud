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

  updateFeature(feature: Feature) {
    return this.http.put(`/rest/features/${feature.id}`, {enabled: feature.enabled})
  }

  deleteFeature(id: string) {
    return this.http.delete<Feature>(`/rest/features/${id}`)
  }

}

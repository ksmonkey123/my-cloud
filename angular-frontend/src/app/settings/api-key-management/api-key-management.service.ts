import {Injectable, OnDestroy} from '@angular/core';
import {map, Observable, Subject, takeUntil} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {BaseLocalDataChangeService} from "../../common/base/base-local-data-change-service.service";

@Injectable()
export class ApiKeyManagementService extends BaseLocalDataChangeService<ApiKey> implements OnDestroy {

  private closer$ = new Subject<void>()

  constructor(private http: HttpClient) {
    super()
  }

  public list(): Observable<ApiKey[]> {
    return this.http.get<ApiKeyDTO[]>('rest/auth/api_keys')
      .pipe(
        takeUntil(this.closer$),
        map(list => list.map(convert))
      )
  }

  public create(name: string, roles: string[]): Observable<ApiKey> {
    return this.http.put<{ key: ApiKeyDTO, tokenString: string }>('rest/auth/api_keys/' + name, {roles: roles})
      .pipe(
        takeUntil(this.closer$),
        map(resp => {
          const key = convert(resp.key)
          key.token = resp.tokenString
          return key
        })
      )
  }

  public delete(name: string): Observable<any> {
    return this.http.delete('rest/auth/api_keys/' + name)
      .pipe(takeUntil(this.closer$))
  }

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
  }

}

function convert(dto: ApiKeyDTO): ApiKey {
  return {
    name: dto.name,
    createdAt: new Date(dto.createdAt),
    authorities: dto.authorities
  }
}

interface ApiKeyDTO {
  name: string
  createdAt: string
  authorities: ApiKeyAuthority[]
}

export interface ApiKey {
  name: string
  createdAt: Date
  authorities: ApiKeyAuthority[]
  token?: string
}

export interface ApiKeyAuthority {
  name: string
  active: boolean
}

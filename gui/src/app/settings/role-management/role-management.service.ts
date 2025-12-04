import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";

@Injectable()
export class RoleManagementService implements OnDestroy {

  constructor(private http: HttpClient) {
  }

  public roleList$ = new BehaviorSubject<Role[]>([])
  private closer$ = new Subject<void>()

  ngOnDestroy() {
    this.closer$.next()
    this.closer$.complete()
    this.roleList$.complete()
  }

  sorter(a: Role, b: Role) {
    return a.name.localeCompare(b.name)
  }

  loadList() {
    this.http.get<Role[]>('/rest/auth/roles')
      .pipe(takeUntil(this.closer$))
      .subscribe((r) => this.roleList$.next(r.sort(this.sorter)))
  }

}

export interface Role {
  name: string
  description?: string
  accountCount: number
}

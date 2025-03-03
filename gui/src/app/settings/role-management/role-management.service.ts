import {Injectable, OnDestroy} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {BehaviorSubject, Subject, takeUntil} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {TranslocoService} from "@jsverse/transloco";

@Injectable()
export class RoleManagementService implements OnDestroy {

  constructor(private http: HttpClient, private toastr: ToastrService, private translation: TranslocoService) {
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

  updateList(role: Role) {
    let list = this.roleList$.value
    list = list.filter(r => r.name !== role.name)
    list.push(role)
    list = list.sort(this.sorter)
    this.roleList$.next(list)
  }

  createRole(name: string, description: string) {
    this.http.put<Role>('/rest/auth/roles/' + name, {description: description})
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: (role) => {
          this.updateList(role)
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("settings.roles.error.creation", {id: name}))
          this.loadList()
        }
      })
  }

  editRole(name: string, data: RoleEditData) {
    this.http.patch<Role>('/rest/auth/roles/' + name, {
      description: data.description,
      enabled: data.enabled
    })
      .pipe(takeUntil(this.closer$))
      .subscribe({
          next: (role) => {
            this.updateList(role)
          },
          error: error => {
            this.toastr.error(error?.error?.message, this.translation.translate("settings.roles.error.creation", {id: name}))
            this.loadList()
          }
        }
      )
  }

  deleteRole(name: string) {
    this.http.delete('/rest/auth/roles/' + name)
      .pipe(takeUntil(this.closer$))
      .subscribe({
        next: _ => {
          this.loadList()
        },
        error: error => {
          this.toastr.error(error?.error?.message, this.translation.translate("settings.roles.error.deletion", {id: name}))
          this.loadList()
        }
      })
  }
}

export interface Role {
  name: string
  description?: string
  enabled: boolean
  accountCount: number
}

export interface RoleEditData {
  description?: string
  enabled?: boolean
}

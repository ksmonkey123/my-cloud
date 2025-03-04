import {Component} from '@angular/core';
import {Role, RoleManagementService} from "./role-management.service";
import {AsyncPipe} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatCard, MatCardContent} from "@angular/material/card";
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
import {MatIcon} from "@angular/material/icon";
import {MatButton, MatMiniFabButton} from "@angular/material/button";
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatDialog} from "@angular/material/dialog";
import {DialogResult, RoleEditPopupDialog} from "./role-edit-popup/role-edit-popup.dialog";
import {SimpleModalService} from "../../common/simple-modal/simple-modal.service";
import {TranslocoPipe, TranslocoService} from "@jsverse/transloco";

@Component({
  selector: 'app-role-management',
  imports: [
    AsyncPipe,
    FormsModule,
    MatCard,
    MatCardContent,
    MatCell,
    MatCellDef,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderRow,
    MatHeaderRowDef,
    MatIcon,
    MatMiniFabButton,
    MatRow,
    MatRowDef,
    MatSlideToggle,
    MatTable,
    ReactiveFormsModule,
    MatHeaderCellDef,
    MatButton,
    TranslocoPipe
  ],
  providers: [RoleManagementService],
  templateUrl: './role-management.component.html',
  styleUrl: './role-management.component.scss'
})
export class RoleManagementComponent {

  displayedColumns = ['name', 'description', 'enabled', 'delete']

  public list$

  constructor(
    public svc: RoleManagementService,
    public dialog: MatDialog,
    public modal: SimpleModalService,
    private transloco: TranslocoService) {
    svc.loadList()
    this.list$ = svc.roleList$
  }

  openEditDialog(role: Role) {
    const dialogRef = this.dialog.open(RoleEditPopupDialog,
      {
        data: {
          create: false,
          name: role.name,
          description: role.description
        }
      })
    dialogRef.afterClosed().subscribe((result: DialogResult) =>
      this.svc.editRole(result.name, {description: result.description})
    )
  }

  openNewRoleDialog() {
    const dialogRef = this.dialog.open(RoleEditPopupDialog,
      {
        data: {
          create: true
        }
      })
    dialogRef.afterClosed().subscribe((result: DialogResult) =>
      this.svc.createRole(result.name, result.description)
    )
  }

  deleteRole(role: Role) {
    this.modal.confirm(
      this.transloco.translate("settings.roles.delete.title"),
      this.transloco.translate("settings.roles.delete.text", {id: role.name})
    ).subscribe(confirm => {
      if (confirm) {
        this.svc.deleteRole(role.name)
      }
    })
  }

  enableRole(name: string, enabled: boolean) {
    this.svc.editRole(name, {enabled: enabled})
  }

}

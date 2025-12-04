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
import {MatSlideToggle} from "@angular/material/slide-toggle";
import {MatDialog} from "@angular/material/dialog";
import {DialogResult, RoleEditPopupDialog} from "./role-edit-popup/role-edit-popup.dialog";
import {SimpleModalService} from "../../common/simple-modal/simple-modal.service";
import {TranslocoPipe} from "@jsverse/transloco";

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
    MatRow,
    MatRowDef,
    MatSlideToggle,
    MatTable,
    ReactiveFormsModule,
    MatHeaderCellDef,
    TranslocoPipe
  ],
  providers: [RoleManagementService],
  templateUrl: './role-management.component.html',
  styleUrl: './role-management.component.scss'
})
export class RoleManagementComponent {

  displayedColumns = ['name', 'description', 'enabled']

  public list$

  constructor(
    public svc: RoleManagementService,
    public dialog: MatDialog,
    public modal: SimpleModalService,
  ) {
    svc.loadList()
    this.list$ = svc.roleList$
  }

  openEditDialog(role: Role) {
    const dialogRef = this.dialog.open(RoleEditPopupDialog,
      {
        data: {
          name: role.name,
          description: role.description
        }
      })
    dialogRef.afterClosed().subscribe((result: DialogResult) =>
      this.svc.editRole(result.name, {description: result.description})
    )
  }

  enableRole(name: string, enabled: boolean) {
    this.svc.editRole(name, {enabled: enabled})
  }

}

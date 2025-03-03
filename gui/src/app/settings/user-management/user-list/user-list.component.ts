import {Component, OnInit} from '@angular/core';
import {Account, UserManagementService} from "../user-management.service";
import {AsyncPipe, NgIf} from "@angular/common";
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
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {AuthService} from "../../../common/auth.service";
import {MatChip} from "@angular/material/chips";
import {ActivatedRoute, Router} from "@angular/router";
import {MatButton} from "@angular/material/button";
import {MatIcon} from "@angular/material/icon";
import {ToastrService} from "ngx-toastr";
import {MatDialog} from "@angular/material/dialog";
import {AddUserPopupDialog, DialogResult} from "./add-user-popup/add-user-popup.dialog";
import {TranslocoPipe} from "@jsverse/transloco";

@Component({
    selector: 'app-user-management',
  imports: [
    AsyncPipe,
    MatCard,
    MatTable,
    MatColumnDef,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCell,
    MatCellDef,
    MatRow,
    MatRowDef,
    MatHeaderRow,
    MatHeaderRowDef,
    MatCardContent,
    MatSlideToggle,
    FormsModule,
    MatChip,
    NgIf,
    MatIcon,
    ReactiveFormsModule,
    MatButton,
    TranslocoPipe
  ],
    templateUrl: './user-list.component.html',
    styleUrl: './user-list.component.scss'
})
export class UserListComponent implements OnInit {

  displayedColumns = ['admin', 'username', 'enabled']
  list$

  constructor(public svc: UserManagementService,
              public auth: AuthService,
              public router: Router,
              public route: ActivatedRoute,
              private toastr: ToastrService,
              private dialog: MatDialog) {
    this.list$ = svc.accountList$
  }

  ngOnInit() {
    this.svc.loadList()
  }


  enableUser(username: string, enabled: boolean) {
    console.log("setting " + username + " enabled " + enabled)
    this.svc.setUserEnabled(username, enabled).subscribe(
      {
        next: () => {
          this.svc.loadList()
        }, error: (error) => {
          this.toastr.error(error?.error?.message, "could not edit user")
        }
      }
    )
  }

  selectUser(user: Account) {
    this.router.navigate([user.username], {relativeTo: this.route})
  }

  openDialog() {
    const dialogRef = this.dialog.open(AddUserPopupDialog)
    dialogRef.afterClosed().subscribe((result: DialogResult) =>
      this.svc.createUser(result.username, result.password).subscribe(
        {
          next: () => {
            this.svc.loadList()
          },
          error: (error) => {
            this.toastr.error(error?.error?.message, "could not create user")
          }
        }
    ))
  }

}

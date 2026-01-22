import {Component} from '@angular/core';
import {ApiKeyManagementService} from "./api-key-management.service";
import {TranslocoDirective, TranslocoPipe} from "@jsverse/transloco";
import {ApiKeyListComponent} from "./api-key-list/api-key-list.component";
import {ApiKeyCreationComponent} from "./api-key-creation/api-key-creation.component";
import {MatCard, MatCardContent} from "@angular/material/card";
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow, MatRowDef, MatTable
} from "@angular/material/table";
import {MatIcon} from "@angular/material/icon";
import {MatMiniFabButton} from "@angular/material/button";
import {MatSlideToggle} from "@angular/material/slide-toggle";

@Component({
  selector: 'app-api-key-management',
  imports: [
    TranslocoDirective,
    ApiKeyListComponent,
    ApiKeyCreationComponent,
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
    TranslocoPipe
  ],
  providers: [ApiKeyManagementService],
  templateUrl: './api-key-management.component.html',
  styleUrl: './api-key-management.component.scss'
})
export class ApiKeyManagementComponent {

}

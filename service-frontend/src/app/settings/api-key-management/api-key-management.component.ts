import {Component} from '@angular/core';
import {ApiKeyManagementService} from "./api-key-management.service";
import {TranslocoPipe} from "@jsverse/transloco";
import {ApiKeyListComponent} from "./api-key-list/api-key-list.component";
import {ApiKeyCreationComponent} from "./api-key-creation/api-key-creation.component";
import {MatAccordion} from "@angular/material/expansion";

@Component({
  selector: 'app-api-key-management',
  imports: [
    ApiKeyListComponent,
    ApiKeyCreationComponent,
    MatAccordion,
    TranslocoPipe
  ],
  providers: [ApiKeyManagementService],
  templateUrl: './api-key-management.component.html',
  styleUrl: './api-key-management.component.scss'
})
export class ApiKeyManagementComponent {

}

import {Component} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";

@Component({
  selector: 'app-api-key-creation',
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIcon
  ],
  templateUrl: './api-key-creation.component.html',
  styleUrl: './api-key-creation.component.scss',
})
export class ApiKeyCreationComponent {

}

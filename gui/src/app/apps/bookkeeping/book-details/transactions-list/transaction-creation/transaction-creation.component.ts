import {Component, Input} from '@angular/core';
import {Book} from "../../../bookkeeping.service";
import {provideNativeDateAdapter} from "@angular/material/core";
import {FormControl, FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {MatFormField, MatLabel} from "@angular/material/form-field";
import {MatInput} from "@angular/material/input";
import {MatDatepicker, MatDatepickerInput, MatDatepickerToggle} from "@angular/material/datepicker";
import {MatDivider} from "@angular/material/divider";

@Component({
  selector: 'app-transaction-creation',
  standalone: true,
  imports: [
    FormsModule,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatFormField,
    MatDatepicker,
    MatDatepickerToggle,
    MatDatepickerInput,
    MatDivider
  ],
  providers: [provideNativeDateAdapter()],
  templateUrl: './transaction-creation.component.html',
  styleUrl: './transaction-creation.component.scss'
})
export class TransactionCreationComponent {

  @Input() book!: Book

  form = new FormGroup({
    date: new FormControl(new Date()),
    text: new FormControl(''),
    tag: new FormControl(''),
    description: new FormControl('')
  })

}

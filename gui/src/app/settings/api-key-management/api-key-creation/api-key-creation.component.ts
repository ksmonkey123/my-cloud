import {Component, computed} from '@angular/core';
import {MatExpansionPanel, MatExpansionPanelHeader, MatExpansionPanelTitle} from "@angular/material/expansion";
import {MatIcon} from "@angular/material/icon";
import {AbstractControl, FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators} from "@angular/forms";
import {MatFormField, MatInput, MatLabel, MatError} from "@angular/material/input";
import {AuthService} from "../../../common/auth.service";
import {MatChip, MatChipSet} from "@angular/material/chips";
import {MatButton} from "@angular/material/button";
import {translate, TranslocoPipe} from "@jsverse/transloco";
import {ApiKeyManagementService} from "../api-key-management.service";
import {map, of, takeUntil} from "rxjs";
import {ToastrService} from "ngx-toastr";
import {BaseDataComponent} from "../../../common/base/base-data.component";
import {mapChangeSet, patchState} from "../../../common/base/base-local-data-change-service.service";

@Component({
  selector: 'app-api-key-creation',
  imports: [
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatIcon,
    MatFormField,
    MatError,
    ReactiveFormsModule,
    MatLabel,
    MatInput,
    MatChipSet,
    MatChip,
    FormsModule,
    MatButton,
    TranslocoPipe,
  ],
  templateUrl: './api-key-creation.component.html',
  styleUrl: './api-key-creation.component.scss',
})
export class ApiKeyCreationComponent extends BaseDataComponent<string[]> {

  form = new FormGroup(
    {
      name: new FormControl('', Validators.compose([Validators.required, (c) => this.validateUniqueName(c)])),
      selected: new FormControl<string[]>([], Validators.compose([Validators.required, Validators.minLength(1)]))
    }
  )

  private validateUniqueName(control: AbstractControl) {
    if (this.data()?.includes(control.value)) {
      return {duplicateName: true}
    }
    return null;
  }

  availableAuthorities = computed(() => {
    return (this.authService.authInfo()?.authorities || []).filter(a => a !== "user" && a !== "admin").sort()
  })

  constructor(private authService: AuthService,
              private service: ApiKeyManagementService,
              private toastr: ToastrService
  ) {
    super();
  }

  override ngAfterViewInit() {
    super.ngAfterViewInit();
    this.loadData(this.service.list().pipe(map(keys => keys.map(k => k.name))))

    this.service.localDataChanges$.pipe(
      takeUntil(this.unsubscribe$)
    ).subscribe(changeSet => {
        this.loadData(of(patchState(this.data(), mapChangeSet(changeSet, k => k.name))))
      }
    )
  }

  toggle(authority: string) {
    let values = this.form.controls.selected.value || [];
    if (values.includes(authority)) {
      // remove authority
      values = values.filter(a => a !== authority);
    } else {
      // add authority
      values.push(authority);
    }
    this.form.controls.selected.setValue(values);
  }

  isSelected(authority: string) {
    return this.form.controls.selected.value?.includes(authority)
  }

  resetForm() {
    this.form.reset();
  }

  createKey() {
    this.service.create(this.form.value.name!, this.form.value.selected!)
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe({
        next: (key) => {
          this.resetForm();
          this.service.setLocalDataChanges({added: [key]});
          this.toastr.success(translate("settings.api-keys.create.success"))
        },
        error: (error) => {
          this.toastr.error(error?.error?.message, translate("settings.api-keys.create.error"))
        }
      })
  }

}

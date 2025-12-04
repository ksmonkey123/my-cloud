import {Routes} from "@angular/router";
import {AccountSettingsComponent} from "./account-settings/account-settings.component";
import {UserManagementService} from "./user-management/user-management.service";
import {RoleManagementService} from "./role-management/role-management.service";
import {UserListComponent} from "./user-management/user-list/user-list.component";
import {UserDetailsComponent} from "./user-management/user-details/user-details.component";
import {RoleManagementComponent} from "./role-management/role-management.component";
import {ApiKeyManagementComponent} from "./api-key-management/api-key-management.component";
import {FeaturesManagementComponent} from "./features-management/features-management.component";
import {FeaturesManagementService} from "./features-management/features-management.service";

export const settings: SettingsInformation = {
  routes: [
    {path: 'account', component: AccountSettingsComponent},
    {path: 'api_keys', component: ApiKeyManagementComponent},
    {
      path: 'users', providers: [UserManagementService, RoleManagementService], children: [
        {path: '', component: UserListComponent},
        {path: ':username', component: UserDetailsComponent},
      ]
    },
    {path: 'roles', component: RoleManagementComponent},
    {path: 'features', component: FeaturesManagementComponent, providers: [FeaturesManagementService]},
  ],
  options: [
    {path: '/account', titleKey: 'navbar.account', auth: 'user', icon: 'person'},
    {path: '/api_keys', titleKey: 'navbar.api-keys', auth: 'user', icon: 'key'},
    {path: '/users', titleKey: 'navbar.users', auth: 'admin', icon: 'manage_accounts'},
    {path: '/roles', titleKey: 'navbar.roles', auth: 'admin', icon: 'page_info'},
    {path: '/features',  titleKey: 'navbar.features',  auth: 'admin',  icon: 'rule'},
  ]
}

export interface SettingsInformation {
  routes: Routes,
  options: SettingsOption[]
}

export interface SettingsOption {
  path: string
  titleKey: string
  auth: string
  icon: string
}

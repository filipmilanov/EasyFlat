import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {DigitalStorageComponent} from "./components/digital-storage/digital-storage.component";
import {
    ItemCreateEditComponent,
    ItemCreateEditMode
} from "./components/digital-storage/item-create-edit/item-create-edit.component";
import {ItemDetailComponent} from "./components/digital-storage/item-detail/item-detail.component";
import {ItemDetailListComponent} from "./components/digital-storage/item-detail-list/item-detail-list.component";
import {StorageItemListDto} from "./dtos/storageItem";
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from "./components/register/register.component";
import {AccountComponent} from "./components/account/account.component";
import {LoginFlatComponent} from "./components/login-flat/login-flat.component";
import {CreateFlatComponent} from "./components/create-flat/create-flat.component";
import {ShoppingListComponent} from "./components/shopping-list/shopping-list.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'digital-storage/:id', children: [
      {path: '', component: DigitalStorageComponent},
      {path: 'item', children: [
          {path: 'create', component: ItemCreateEditComponent, data: {mode: ItemCreateEditMode.create}},
          {path: ':name/list', children:[
            {path: '', component: ItemDetailListComponent },
            {path: ':id/detail', component: ItemDetailComponent},
              {path: ':id/edit', component: ItemCreateEditComponent, data: {mode: ItemCreateEditMode.edit}},
            ]}
        ]}
  ]},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'shopping-list/:id', children: [
      {path: '', component: ShoppingListComponent}
    ]},
  {path: 'register', component: RegisterComponent},
  {path: 'account', component: AccountComponent},
  {path: 'wgLogin', component: LoginFlatComponent},
  {path: 'wgCreate', component: CreateFlatComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})

export class AppRoutingModule {
}

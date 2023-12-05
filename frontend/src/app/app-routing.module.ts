import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
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
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})

export class AppRoutingModule {
}

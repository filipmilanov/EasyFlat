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
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {RegisterComponent} from "./components/register/register.component";
import {AccountComponent} from "./components/account/account.component";
import {LoginFlatComponent} from "./components/login-flat/login-flat.component";
import {CreateFlatComponent} from "./components/create-flat/create-flat.component";
import {ShoppingListComponent} from "./components/shopping-list/shopping-list.component";
import {
  ShoppingItemCreateEditComponent
} from "./components/shopping-list/shopping-item-create-edit/shopping-item-create-edit.component";
import {
  ShoppingListCreateComponent
} from "./components/shopping-list/shopping-list-create/shopping-list-create.component";
import computeOffsets from "@popperjs/core/lib/utils/computeOffsets";
import {CookingComponent} from "./components/cooking/cooking.component";
import {CookbookComponent} from "./components/cookbook/cookbook.component";
import {CookbookCreateComponent, CookbookMode} from "./components/cookbook/cookbook-create/cookbook-create.component";
import {RecipeDetailComponent} from "./components/cooking/recipe-detail/recipe-detail.component";
import {CookbookDetailComponent} from "./components/cookbook/cookbook-detail/cookbook-detail.component";
import {ShoppingListsComponent} from "./components/shopping-list/shopping-lists/shopping-lists.component";
import {ExpenseCreateEditComponent} from "./components/finance/expense-create-edit/expense-create-edit.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {
    path: 'digital-storage', children: [
      {path: '', component: DigitalStorageComponent},
      {
        path: 'item', children: [
          {path: 'create', component: ItemCreateEditComponent, data: {mode: ItemCreateEditMode.create}},
          {
            path: ':name/list', children: [
              {path: '', component: ItemDetailListComponent},
              {path: ':id/detail', component: ItemDetailComponent},
              {path: ':id/edit', component: ItemCreateEditComponent, data: {mode: ItemCreateEditMode.edit}},
            ]
          }
        ]
      }
    ]
  },
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {
    path: 'shopping-lists', children: [
      {path: '', component: ShoppingListsComponent},
      {
        path: 'list', children: [
          {path: 'create', component: ShoppingListCreateComponent}
        ]
      },
      {
        path: 'list/:id', children: [
          {path: '', component: ShoppingListComponent},
          {
            path: 'item', children: [
              {path: 'create', component: ShoppingItemCreateEditComponent, data: {mode: ItemCreateEditMode.create}},
              {path: ':id/edit', component: ShoppingItemCreateEditComponent, data: {mode: ItemCreateEditMode.edit}},
            ]
          },
        ]
      },
    ]
  },
  {path: 'register', component: RegisterComponent},
  {path: 'account', component: AccountComponent},
  {path: 'wgLogin', component: LoginFlatComponent},
  {path: 'wgCreate', component: CreateFlatComponent},
  {
    path: 'expense', children: [
      {path: '', component: DigitalStorageComponent},
      {path: 'create', component: ExpenseCreateEditComponent, data: {mode: ItemCreateEditMode.create}},
    ]
  },
  {
    path: 'cooking', children: [
      {path: '', component: CookingComponent},
      {path: ':id/detail', component: RecipeDetailComponent}
    ]
  },
  {
    path: 'cookbook', children: [
      {path: '', component: CookbookComponent},
      {path: 'create', component: CookbookCreateComponent, data: {mode: CookbookMode.create}},
      {path: ':id/edit', component: CookbookCreateComponent, data: {mode: CookbookMode.edit}},
      {path: ':id/detail', component: CookbookDetailComponent}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})

export class AppRoutingModule {
}

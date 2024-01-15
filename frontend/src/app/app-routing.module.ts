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
  ShoppingItemCreateEditComponent, ShoppingItemCreateEditMode
} from "./components/shopping-list/shopping-item-create-edit/shopping-item-create-edit.component";
import {
  ShoppingListCreateComponent
} from "./components/shopping-list/shopping-list-create/shopping-list-create.component";
import {CookingComponent} from "./components/cooking/cooking.component";
import {CookbookComponent} from "./components/cookbook/cookbook.component";
import {CookbookCreateComponent, CookbookMode} from "./components/cookbook/cookbook-create/cookbook-create.component";
import {RecipeDetailComponent} from "./components/cooking/recipe-detail/recipe-detail.component";
import {CookbookDetailComponent} from "./components/cookbook/cookbook-detail/cookbook-detail.component";
import {ShoppingListsComponent} from "./components/shopping-list/shopping-lists/shopping-lists.component";
import {EventsComponent} from "./components/events/events.component";
import {EventsCreateComponent, EventsMode} from "./components/events/events-create/events-create.component";
import {
  ExpenseCreateEditComponent,
  ExpenseCreateEditMode
} from "./components/finance/expense-create-edit/expense-create-edit.component";
import {FinanceComponent} from "./components/finance/finance.component";
import {ExpenseDetailComponent} from "./components/finance/expense-detail/expense-detail.component";
import {ExpenseOverviewComponent} from "./components/finance/expense-overview/expense-overview.component";
import {HaushaltPlanComponent} from "./components/haushalt-plan/haushalt-plan.component";
import {ChorePreferenceComponent} from "./components/haushalt-plan/chore-preference/chore-preference.component";
import {AllChoreComponent} from "./components/haushalt-plan/all-chore/all-chore.component";
import {MyChoresComponent} from "./components/haushalt-plan/my-chores/my-chores.component";
import {NewChoreComponent} from "./components/haushalt-plan/new-chore/new-chore.component";
import {LeaderboardComponent} from "./components/haushalt-plan/leaderboard/leaderboard.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {
    path: 'digital-storage', children: [
      {path: '', component: DigitalStorageComponent},
      {path: ':name', component: ItemDetailListComponent }
  ]},
  { path: 'item', children: [
      { path: 'create', component: ItemCreateEditComponent, data: { mode: ItemCreateEditMode.create } },
      { path: ':id/detail', component: ItemDetailComponent },
      { path: ':id/edit', component: ItemCreateEditComponent, data: { mode: ItemCreateEditMode.edit } },
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
              {path: 'create', component: ShoppingItemCreateEditComponent, data: {mode: ShoppingItemCreateEditMode.create}},
              {path: ':id/edit', component: ShoppingItemCreateEditComponent, data: {mode: ShoppingItemCreateEditMode.edit}},
            ]
          },
        ]
      },
    ]
  },
  {path: 'register', component: RegisterComponent},
  {path: 'chores', children: [
      {path: '', component: HaushaltPlanComponent},
      {path: 'preference', component: ChorePreferenceComponent},
      {path: 'all', component: AllChoreComponent},
      {path: 'my', component: MyChoresComponent},
      {path: 'add', component: NewChoreComponent},
      {path: 'leaderboard', component: LeaderboardComponent},
    ]},
  {path: 'account', component: AccountComponent},
  {path: 'wgLogin', component: LoginFlatComponent},
  {path: 'wgCreate', component: CreateFlatComponent},
  {
    path: 'finance', children: [
            {path: '', component: FinanceComponent},
        ]
    },
    {
      path: 'expense', children: [
        {path: '', component: ExpenseOverviewComponent},
        {path: 'create', component: ExpenseCreateEditComponent, data: {mode: ExpenseCreateEditMode.create}},
        {path: ':id/edit', component: ExpenseCreateEditComponent, data: {mode: ExpenseCreateEditMode.edit}},
        {path: ':id/detail', component: ExpenseDetailComponent},
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
    },
  {
    path: 'events', children: [
      {path: '', component: EventsComponent},
      {path: 'create', component: EventsCreateComponent, data: {mode: EventsMode.create}},
      {path: ':id/edit', component: EventsCreateComponent, data: {mode: EventsMode.edit}}
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})

export class AppRoutingModule {
}

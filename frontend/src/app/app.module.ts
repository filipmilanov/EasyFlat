import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {MessageComponent} from './components/message/message.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import {DigitalStorageComponent} from './components/digital-storage/digital-storage.component';
import {ItemCardComponent} from './components/digital-storage/item-card/item-card.component';
import {ItemCreateEditComponent} from './components/digital-storage/item-create-edit/item-create-edit.component';
import {ItemDetailComponent} from './components/digital-storage/item-detail/item-detail.component';
import {ToastrModule} from "ngx-toastr";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AutocompleteComponent} from './components/utils/autocomplete/autocomplete.component';
import {ItemDetailListComponent} from './components/digital-storage/item-detail-list/item-detail-list.component';
import {RegisterComponent} from './components/register/register.component';
import {AccountComponent} from './components/account/account.component';
import {LoginFlatComponent} from "./components/login-flat/login-flat.component";
import {CreateFlatComponent} from './components/create-flat/create-flat.component';
import {FinanceComponent} from './components/finance/finance.component';
import {ExpenseCreateEditComponent} from './components/finance/expense-create-edit/expense-create-edit.component';
import {
  RadioButtonsComponentComponent
} from './components/utils/radio-buttons-component/radio-buttons-component.component';
import {ShowUserForExpenseComponent} from './components/utils/show-user-for-expense/show-user-for-expense.component';
import {UserDropdownComponent} from './components/utils/user-dropdown/user-dropdown.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    LoginComponent,
    MessageComponent,
    DigitalStorageComponent,
    ItemCardComponent,
    ItemCreateEditComponent,
    ItemDetailComponent,
    AutocompleteComponent,
    ItemDetailListComponent,
    RegisterComponent,
    AccountComponent,
    LoginFlatComponent,
    CreateFlatComponent,
    FinanceComponent,
    ExpenseCreateEditComponent,
    RadioButtonsComponentComponent,
    RadioButtonsComponentComponent,
    ShowUserForExpenseComponent,
    UserDropdownComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    ToastrModule.forRoot(),
    BrowserAnimationsModule,
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})

export class AppModule {
}

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
import { CreateFlatComponent } from './components/create-flat/create-flat.component';
import { ShoppingListComponent } from './components/shopping-list/shopping-list.component';
import {ShoppingItemCreateEditComponent} from "./components/shopping-list/shopping-item-create-edit/shopping-item-create-edit.component";
import {ColorPickerModule} from "ngx-color-picker";
import { ShoppingListCreateComponent } from './components/shopping-list/shopping-list-create/shopping-list-create.component';
import {CookingComponent} from './components/cooking/cooking.component';
import {RecipeCardComponent} from './components/cooking/recipe-card/recipe-card.component';
import {NgOptimizedImage} from "@angular/common";
import {CookbookComponent} from './components/cookbook/cookbook.component';
import {CookbookCardComponent} from './components/cookbook/cookbook-card/cookbook-card.component';
import {RecipeDetailComponent} from './components/cooking/recipe-detail/recipe-detail.component';
import {CookbookCreateComponent} from './components/cookbook/cookbook-create/cookbook-create.component';
import {CookbookDetailComponent} from './components/cookbook/cookbook-detail/cookbook-detail.component';
import {CookbookModalComponent} from './components/cookbook/cookbook-modal/cookbook-modal.component';
import {CookingModalComponent} from './components/cooking/cooking-modal/cooking-modal.component';
import {LOAD_WASM, NgxScannerQrcodeModule} from "ngx-scanner-qrcode";
import {ConfirmDeleteDialogComponent} from "./components/utils/confirm-delete-dialog/confirm-delete-dialog.component";
import { ShoppingListsComponent } from './components/shopping-list/shopping-lists/shopping-lists.component';
import { ShoppingListCardComponent } from './components/shopping-list/shopping-list-card/shopping-list-card.component';
import { MatchingModalComponent } from './components/cooking/matching-modal/matching-modal.component';
import { MatchingModalCookbookComponent } from './components/cookbook/matching-modal-cookbook/matching-modal-cookbook.component';


LOAD_WASM().subscribe();


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
    ShoppingListComponent,
    ShoppingItemCreateEditComponent,
    ShoppingListCreateComponent,
    CookingComponent,
    RecipeCardComponent,
    CookbookComponent,
    CookbookCardComponent,
    RecipeDetailComponent,
    CookbookCreateComponent,
    CookbookDetailComponent,
    CookbookModalComponent,
    CookingModalComponent,
    ConfirmDeleteDialogComponent,
    ShoppingListsComponent,
    ShoppingListCardComponent,
    MatchingModalComponent,
    MatchingModalCookbookComponent
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
    ColorPickerModule,
    NgOptimizedImage,
    NgxScannerQrcodeModule
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})

export class AppModule {
}

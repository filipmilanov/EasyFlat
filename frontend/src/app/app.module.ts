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

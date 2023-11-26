import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {MessageComponent} from './components/message/message.component';
import {DigitalStorageComponent} from "./components/digital-storage/digital-storage.component";
import {ItemCreateComponent} from "./components/digital-storage/item-create/item-create.component";

const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'message', canActivate: mapToCanActivate([AuthGuard]), component: MessageComponent},
  {path: 'digital-storage', children: [
      {path: '', component: DigitalStorageComponent},
      {path: 'item', children: [
          {path: 'new', component: ItemCreateComponent}
        ]}
  ]}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})

export class AppRoutingModule {
}

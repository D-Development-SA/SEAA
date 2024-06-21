import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {StorageService} from "../services/storage.service";
import {UserService} from "../services/user.service";

export const authGuard: CanActivateFn = (route, state) => {
  const login = inject(UserService);
  const storage = inject(StorageService);
  const navigate = inject(Router);

  if (storage.getData('client')){
    return true;
  }else {
    navigate.navigateByUrl('login').finally(() => login.logout());
    return false;
  }
};

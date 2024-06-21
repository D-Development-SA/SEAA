import {CanActivateChildFn, CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {UserService} from "../services/user.service";

export const rolesGuard: CanActivateChildFn = (route, state) => {
  let user = inject(UserService);
  let navigate = inject(Router);

  if (user.containsRol(route.data['role'])){
    return true;
  }
  navigate.navigateByUrl('main');
  return false;
};

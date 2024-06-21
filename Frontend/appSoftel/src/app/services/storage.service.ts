import { Injectable } from '@angular/core';
import * as CryptoJS from 'crypto-js';

@Injectable({
  providedIn: 'root'
})
export class StorageService {

  private key: string = 'encrypt!135790';

  constructor() { }

  setData(key: string, value: any, oneTime: boolean){
    if(key && (value != null || value != undefined)){
      
      const encryptedValue = CryptoJS.AES.encrypt(JSON.stringify(value), this.key).toString();

      const newValue = {
        value: encryptedValue,
        oneTime: oneTime
      }

      sessionStorage.setItem(key, JSON.stringify(newValue));
    }
  }

  getData(key: string){
    if (key) {
      let value = JSON.parse(sessionStorage.getItem(key)!);

      if (value && value.hasOwnProperty('oneTime')) {
        let oneTime: boolean = value['oneTime'];

        if (oneTime) {
          sessionStorage.removeItem(key);
        }
        let decryptedValue = CryptoJS.AES.decrypt(value.value, this.key).toString(CryptoJS.enc.Utf8);
        return JSON.parse(decryptedValue);
      }
    }
    return null;
  }

  clear(){
    sessionStorage.clear();
  }
}

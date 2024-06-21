import { Component } from '@angular/core';
import { contentFade, fade, initCard, startText, textHeader } from '../../animations/animations';
import { UserService } from '../../services/user.service';
import { NavigationEnd, Router } from '@angular/router';
import { StorageService } from 'src/app/services/storage.service';

@Component({
  selector: 'app-quick-start-guide',
  templateUrl: './quick-start-guide.component.html',
  styleUrls: ['./quick-start-guide.component.css'],
  animations: [initCard, textHeader, startText, contentFade, fade],
})
export class QuickStartGuideComponent {
  title: string[] = [
    'Estadísticas',
    'Cuestionario',
    'Registro de consultas',
    'Especialista',
    'Comunicación con los administradores',
  ];
  text: string[] = [
    'Ver estadísticas de consultas hechas por ti y más información referente.',
    'Detectar posibles enfermedades a través de los síntomas que definas. Esto se realiza en la sección de Cuestionario',
    'Ver todas las consultas que hayas guardado anteriormente y continuar alguna de ellas. Puedes realizar esta acción en registro.',
    'Algo muy interesante es que puedes solicitar ser un especialista. Esto te proveerá de ciertos privilegios si antes eres aceptado a serlo por los administradores.',
    'Puedes enviar información al administrador en caso de querer comunicarte con él. Al mismo le llegará una notificación con tu mensaje. Para esto ir a la sección de Ayuda.',
    'Para más información le recomendamos que viaje a la sección de ayuda, ahí encontrará información relevante sobre la app, incluyendo una guía de pasos.',
  ];
  images: string[][] = [
    ['graphHome.svg'],
    ['cuestionnarie.svg', 'questions.svg'],
    ['register.svg'],
    ['specialist.svg'],
    ['information.svg'],
  ]
  count: number = 0;
  btnDisabledRight: boolean = false;
  btnDisabledLeft: boolean = false;

  constructor(private storage: StorageService, private root: Router){
  }

  next(){
    if(this.count < this.text.length - 1){
      this.btnDisabledLeft = false;
      this.count++;
    }
    if (this.count == this.text.length - 1) {
      this.btnDisabledRight = true;
    }
  }

  previous(){
    if(this.count > 0){
      this.btnDisabledRight = false;
      this.count--;
    }
    if (this.count == 0) {
      this.btnDisabledLeft = true;
    }
  }

  finish(){
    this.root.navigateByUrl('main');
  }

  goToHelp(){
    this.storage.setData('active', 6, true);
    this.storage.setData('btntoogle', false, false);
    this.root.navigateByUrl('main');
  }
}

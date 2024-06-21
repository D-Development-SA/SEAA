import {AfterViewInit, Component, ElementRef, OnInit, QueryList, ViewChild, ViewChildren} from '@angular/core';
import { Notification } from '../../classes/Notification';
import { UserService } from '../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTabGroup} from "@angular/material/tabs";
import {StepGuideComponent} from "../step-guide/step-guide.component";

@Component({
  selector: 'app-help-us',
  templateUrl: './help-us.component.html',
  styleUrls: ['./help-us.component.css']
})
export class HelpUsComponent implements OnInit, AfterViewInit{
  @ViewChildren('element') myElements: QueryList<ElementRef> | undefined;
  @ViewChild('tabGroup') tabGroup: MatTabGroup | undefined;
  @ViewChild('stepGuide') stepGuide: StepGuideComponent | undefined;

  idUser: number = -1;
  userName: string = '';
  content: string = '';
  load: boolean = false;

  goToElement(id: string): void {
    const element = this.myElements!.find(element => element.nativeElement.id === id);
    if (element) {
      element.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }

    const elementInsideComponent = this.stepGuide!.myElements!.find(element => element.nativeElement.id === id);
    if (elementInsideComponent) {
      elementInsideComponent.nativeElement.scrollIntoView({ behavior: 'smooth' });
    }
  }

  infoList: Info[] = [
    {id: 'Inicio', title: 'Inicio',
    content: 'La sección de inicio se muestra una especie de dashboard donde puede ver el total de consultas realizadas, '
    + 'estas además mostradas un una gráfica evaluada en meses.Cuenta con sección de resultados, estos son las consultas guardadas por parte del usuario '
    + ', presionado alguna de estas se les muestra el cuestionario correspondiente.'
    },
    {id: 'Comportamiento', title: 'Comportamiento *Solo Especialistas', content: 'Esta sección solo corresponde a los especialistas. En ella se puede '
    + 'observar el comportamiento de los sistemas expertos asociados a ti. La información mostrada corresponde al sistema experto seleccionado. Usted puede '
    + 'leer las consultas realizadas por el usuario asi como su registro de preguntas y respuestas. También se mostrará el sistema experto compartido por otro '
    + 'especialista y podrá ver la información como si fuera su sistema experto. Puede agragarle de manera opcional una nota a las preguntas y respuestas en la '
    + 'parte superior de las mismas.'
    },
    {id: 'Cuestionario', title: 'Cuestionario', content: 'La sección Cuestionario es donde a usted se le realizan preguntas para determinar una conclusión de su '
    + 'posible enfermadad. El resultado es mostrado en posiblidades ya que es un estimado de la enfermadad que usted podría enfrentar. Las consultas pueden ser '
    + 'guardadas, canceladas y realizar una nueva, todo esto a traves de los botnoes correspondientes, estos se encuentran en la parte inferior derecha de la página. '
    + 'En caso de que usted haya realizado una respuesta equivocada solo debe presionar cancelar en dicha respuesta. Mientras más información proporcione más acertada '
    + 'la respuesta en la conclusión. El sistema de preguntas puede ser poco extenso dependiendo de las respuestas proporcionadas.'
    },
    {id: 'Registro', title: 'Registro', content: 'En esta sección todas las consultas guardadas por el usuario correspondiente serán mostradas como conclusión. Para ver '
    + 'más detalles como las preguntas realizadas y las respuestas otorgadas, puede acceder a través del botón de Ver detalles correspondiente a esa consulta. Podrá ver la fecha '
    + 'y hora de la consulta realizada. No podrá eliminar ninguna de estas.'
    },
    {id: 'Avanzado', title: 'Avanzado', content: 'Todo el control referente a el usuario es controlado y puede ser manejado en esta página. Puede modificar la mayoría de los datos de los '
    + 'usuarios como datos personales, las consultas y si es especialista los sistemas expertos asociados. El otorgamiento de roles tambien se encuentra en ella. Los cambios pueden ser guardados '
    + 'o cancelados de manera global (Botón guardar todos los usuarios al final de la página o acceso directo en la parte inferior derecha) y local ( cuando expande el acordeon para ver '
    + 'los datos, al final de este se encuentra el botón Aceptar). En la cabecera del acordeon, del usuario puede encontrar el id, nombre y apellidos del usuario correspondiente, además de poder '
    + 'de poder eliminarlo, desactivarlo, abrir para editar y registro (consultas realizadas), estos últimos en la parte derecha.'
    },
    {id: 'Opciones', title: 'Opciones', content: 'En la sección puede crear un sistema experto y asociarlo a usted (esta última de manera automática), para realizar esta operación obligatoriamente usted '
    + 'tiene que ser especialista. Puede solicitar ser especialista y ver además si lo es o no '
    + '( Todo depende de la respuesta del Admin ). Si es especialista puede compartir su sistema experto a otros especialista, completando los pasos de la sección. En la última sección de '
    + 'de la página puede ver una síntesis de su información.'
    },
    {id: 'Ayuda', title: 'Ayuda', content: 'Se muestra toda la información referente a la aplicación. Puede enviar una notificación al Admin.'},
    {id: 'Acerca', title: 'Acerca de Nosotros', content: 'Toda información referente a la empresa y al equipo de trabajo.'},
  ]

  idStepGuide: Info[] =[
    { id: 'realizeQuestionnaire', title: 'Realizar cuestionario'},
    { id: 'savedQuery', title: 'Ver consultas guardadas'},
    { id: 'continueSavedQuery', title: 'Continuar consulta guardada'},
    { id: 'applySpecialist', title: 'Solicitar ser especialista'},
    { id: 'editData', title: 'Editar mis datos de perfil'},
    { id: 'createSE', title: 'Crear Sistema Experto'},
    { id: 'shareSE', title: 'Compartir Sistema Experto'},
    { id: 'behavior', title: 'Comportamiento de SE'},
    { id: 'usersSE', title: 'Usuarios relacionados con SE'},
  ]

  list: any = this.idStepGuide;

  constructor(private userServ: UserService, private snack: MatSnackBar){}

  ngOnInit(): void {
    this.userServ.userObserver.subscribe(user => {
      this.idUser = user.id!;
      this.userName = user.name + ' ' + user.lastName;
    });
  }

  send(){
    this.load = true;
    let notificacion: Notification = {
      iduser: this.idUser,
      title: 'Información de ' + this.userName,
      content: this.content,
      type: 'error',
      view: false,
      date: new Date()
    }

    this.userServ.postInformationError(notificacion).subscribe({
      complete: () => {
        this.load = false;
        this.snack.open('Notificación enviada', 'OK', {duration: 2000});
      },
      error: () => {
        this.snack.open('No se pudo enviar notificación', 'OK', {duration: 2000});
        this.load = false;
      }
    });
  }

  ngAfterViewInit(): void {
    this.tabGroup!.selectedTabChange.subscribe(event => {
      const index = event.index;
      if (index === 0){
        this.list = this.idStepGuide;
      }else{
        this.list = this.infoList;
      }
    })

  }
}

export interface Info{
  id: string;
  title: string;
  content?: string;
}

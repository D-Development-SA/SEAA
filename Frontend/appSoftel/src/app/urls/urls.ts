const dominio: string = 'http://localhost:8080/';
const api: string = 'api/v1/';
const api_user: string = 'user/';
const api_specialist: string = 'specialist/';
const api_seaa: string = 'seaa/';
const api_log: string = 'log';
const api_notification: string = 'notification/';

export class urls {
  static login: string = dominio + 'login';
  static logout: string = dominio + 'logout';

  static findAllUser: string = dominio + api.substring(0, api.length - 1);
  static findAllRoles: string = dominio + api + 'roles';
  static findAllUserSpecialist: string = dominio + api + api_specialist + 'findAllUserSpecialist';
  static findAllUserPage: string = dominio + api + api_user + 'page/';
  static findUserById: string = dominio + api + 'searchID/';
  static quantityQueryByMonths: string = dominio + api + api_user + 'graph';
  static enabledUser: string = dominio + api + 'enabledUser/';
  static updateUser: string = dominio + api + api_user + 'update';
  static update: string = dominio + api + 'update/';
  static addSeaaUser: string = dominio + api + api_specialist + 'addSeaa/';
  static createUser: string = dominio + api + 'create';
  static saveAllUser: string = dominio + api + 'saveAllUser';
  static deleteUser: string = dominio + api + 'delete/';
  static deleteAllUser: string = dominio + api + 'deleteAll';
  static addRolSpecialist: string = dominio + api + 'addRolSpecialist/';

  static notifications: string = dominio + api + api_notification.substring(0, api_notification.length - 1);
  static requestToBeSpecialist: string = dominio + api + api_notification + api_user + "create/requestToBeSpecialist";
  static postInformationError: string = dominio + api + api_notification + api_user + "create/informationError";
  static getView: string = dominio + api + api_notification + "view/";

  static findAllSeaa: string = dominio + api + api_seaa + api_user.substring(0, api_seaa.length - 1);
  static findSeaaById: string = dominio + api + api_seaa + api_specialist + "findSeaaById/";
  static newQuery: string = dominio + api + api_seaa + api_user + 'newQuery/';
  static queryVar: string = dominio + api + api_seaa + api_user + 'queryVar/';
  static queryProp: string = dominio + api + api_seaa + api_user + 'queryProp/';
  static updateAnswerVar: string = dominio + api + api_seaa + api_user + 'updateAnswerVar/';
  static updateAnswerProp: string = dominio + api + api_seaa + api_user + 'updateAnswerProp/';
  static seaaGraph: string = dominio + api + api_seaa + api_specialist + 'graph/';
  static relationUserSeaa: string = dominio + api + api_seaa + api_specialist + 'relationUserSeaa/';
  static saveQuery: string = dominio + api + api_seaa + api_user + 'save/';
  static loadQuerySaved: string = dominio + api + api_seaa + api_user + 'upload/';
  static updateQuestionOfQuestionnaire: string = dominio + api + api_seaa + api_specialist + 'updateQuestion/';
  static shareSeaa: string = dominio + api + api_seaa + api_specialist + 'shareSeaa/';
  static createSeaa: string = dominio + api + api_seaa + api_specialist + 'create/'
  static changeSeaa: string = dominio + api + api_seaa + api_specialist + 'changeSeaa/'
  static uploadSeaaRarOrZip: string = dominio + api + api_seaa + api_specialist + 'uploadSeaaRarOrZip/'

  static allLogs: string = dominio + api + api_log;

}

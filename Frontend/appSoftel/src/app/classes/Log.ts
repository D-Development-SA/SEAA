interface LogUserContent {
  id: number;
  idFolder: number;
}

export interface Log{
  id: number;
  name: string;
  lastName: string;
  ci: string;
  date: Date;
  method: number;
  logUserContent: LogUserContent;
}

export class Method{
  public static CREATE: number = 1;
  public static UPDATE: number = 2;
  public static DELETE: number = 3;
}

import { Query } from "./Query";
import { Rol } from "./Rol";
import { Specialist } from "./Specialist";

export interface User{
    id?: number;
    name: string;
    lastName: string;
    password: string;
    date?: Date;
    enabled: boolean;
    roles: Rol[];
    queries: Query[];
    specialist?: Specialist;
    phoneNumber: string;
}
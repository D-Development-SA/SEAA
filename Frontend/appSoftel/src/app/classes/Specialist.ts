import { Seaa } from "./Seaa";

export interface Specialist{
    id?: number;
    ci: string;
    knowledgeArea: string;
    scientificCategory: string;
    professionalRegister: string;
    biography: string;
    seaaList: Seaa[];
    seaaShared: number[];
}
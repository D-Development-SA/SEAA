import { Item } from "./Item";

export interface Question{
    var: string;
    domi: string;
    parti: string;
    type: string;
    certi: string;
    neither: string;
    doubleClick: string;
    text: string;
    prop: string;
    list: Item[];
    concluF: Item[];
    concluP: Item[];
    returnVexMain: string;
    return0: string;
    note: string;
}
import { notificationType } from "../components/notification/notification.component";

export interface Notification{
    id?: number;
    iduser?: number;
    title: string;
    content: string;
    type: notificationType;
    view: boolean;
    date: Date;
    showBtn?: boolean;
}
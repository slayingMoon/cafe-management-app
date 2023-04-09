import { Injectable } from "@angular/core";

export interface Menu {
    state: string;
    name: string;
    icon: string;
    role: string;
}

//google icons, choose an icon and put it's name next to icon
//displays the button in the sidebar
const MENUITEMS = [
    { state: 'dashboard', name: 'Dashboard', icon: 'dashboard', role: '' },
    { state: 'category', name: 'Manage Category', icon: 'category', role: 'ROLE_ADMIN' },
    { state: 'product', name: 'Manage Product', icon: 'inventory_2', role: 'ROLE_ADMIN' },
    { state: 'order', name: 'Manage Order', icon: 'list_alt', role: '' },
    { state: 'bill', name: 'View Bill', icon: 'import_contacts', role: '' },
    { state: 'user', name: 'Manage User', icon: 'people', role: 'ROLE_ADMIN' }
];

@Injectable()
export class MenuItems {

    getMenuItem(): Menu[] {
        return MENUITEMS;
    }

}
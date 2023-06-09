import { Component, OnInit } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from 'src/app/services/snackbar.service';
import { UserService } from 'src/app/services/user.service';
import { GlobalConstants } from 'src/app/shared/global-constants';
import {MatDialog, MatDialogConfig} from "@angular/material/dialog";
import {Router} from "@angular/router";
import {UserEditComponent} from "../dialog/user-edit/user-edit.component";

@Component({
  selector: 'app-manage-user',
  templateUrl: './manage-user.component.html',
  styleUrls: ['./manage-user.component.scss']
})
export class ManageUserComponent implements OnInit {
  displayedColumns: string[] = ['name', 'email', 'contactNumber', 'roleName', 'status'];
  dataSource: any;
  responseMessage: any;

  constructor(private ngxService: NgxUiLoaderService,
    private userService: UserService,
    private snackbarService: SnackbarService,
    private dialog: MatDialog,
    private router: Router) { }

  ngOnInit(): void {
    this.ngxService.start();
    this.tableData();
  }

  tableData() {
    this.userService.getUsers().subscribe((response: any) => {
      this.ngxService.stop(); //close loader overlay
      this.dataSource = new MatTableDataSource(response);
      //if any error occurs
    }, (error: any) => {
      this.ngxService.stop(); //close loader overlay

      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }

      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  handleChangeAction(status: any, id: any) {
    this.ngxService.start();
    var data = {
      status: status.toString(),
      id: id
    }

    this.userService.update(data).subscribe((response: any) => {
      this.ngxService.stop();
      this.responseMessage = response?.message;
      this.snackbarService.openSnackBar(this.responseMessage, "success");
    }, (error: any) => {
      this.ngxService.stop();

      if(error.error?.message) {
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }

      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  handleEditAction(values: any) {
    const dialogConfig = new MatDialogConfig(); //create new Dialog for the add product operation
    dialogConfig.data = {
      action: 'Edit',
      data: values
    };
    dialogConfig.width = '850px';

    //tell the dialog to refer to ProductComponent, this will allow the UI to open the product component html template
    const dialogRef = this.dialog.open(UserEditComponent, dialogConfig);
    this.router.events.subscribe(() => {
      dialogRef.close();
    });

    const sub = dialogRef.componentInstance.onUserEdit.subscribe((response) => {
      this.tableData();
    });
  }

}

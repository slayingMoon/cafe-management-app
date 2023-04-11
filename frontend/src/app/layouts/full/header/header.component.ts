import { Component } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { ChangePasswordComponent } from 'src/app/material-component/dialog/change-password/change-password.component';
import { ConfirmationComponent } from 'src/app/material-component/dialog/confirmation/confirmation.component';
import {ChangeUsernameComponent} from "../../../material-component/dialog/change-username/change-username.component";
import {GlobalConstants} from "../../../shared/global-constants";
import {UserService} from "../../../services/user.service";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {SnackbarService} from "../../../services/snackbar.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: []
})
export class AppHeaderComponent {
  role: any;
  responseMessage: any;
  constructor(private router: Router,
    private userService: UserService,
    private ngxService: NgxUiLoaderService,
    private snackbarService: SnackbarService,
    private dialog: MatDialog) {

  }

  logout() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      message: 'Logout'
    };
    const dialogRef = this.dialog.open(ConfirmationComponent, dialogConfig);
    //If user chooses option YES, to logout, close the dialog, clear token and navigate to home page
    const sub = dialogRef.componentInstance.onEmitStatusChange.subscribe((user) => {
      dialogRef.close();
      localStorage.clear();
      this.router.navigate(['/']); //router navigate to home page
    })
  }

  changePassword() {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = "550px";
    this.dialog.open(ChangePasswordComponent, dialogConfig);
  }

  changeUsername() {
    this.userData();
  }

  userData() {
    this.userService.getUser().subscribe((response: any) => {
      const dialogConfig = new MatDialogConfig();
      dialogConfig.data = {
        data: response
      }
      dialogConfig.width = "550px";
      this.dialog.open(ChangeUsernameComponent, dialogConfig);
      //if any error occurs
    }, (error: any) => {
      this.ngxService.stop();
      if (error.error?.message) {
        this.responseMessage = error.error?.message;
      } else {
        this.responseMessage = GlobalConstants.genericError;
      }

      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

}

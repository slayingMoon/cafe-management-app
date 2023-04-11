import {Component, OnInit, AfterContentInit, Inject} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {UserService} from "../../../services/user.service";
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {NgxUiLoaderService} from "ngx-ui-loader";
import {SnackbarService} from "../../../services/snackbar.service";
import {GlobalConstants} from "../../../shared/global-constants";
import jwt_decode from 'jwt-decode';

@Component({
  selector: 'app-change-username',
  templateUrl: './change-username.component.html',
  styleUrls: ['./change-username.component.scss']
})
export class ChangeUsernameComponent implements OnInit {
  changeUsernameForm: any = FormGroup;
  responseMessage: any;
  token: any = localStorage.getItem('token');
  tokenPayload: any;

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any,
              private formBuilder: FormBuilder,
              private userService: UserService,
              private dialogRef: MatDialogRef<ChangeUsernameComponent>,
              private ngxService: NgxUiLoaderService,
              private snackbarService: SnackbarService
  ) {
    this.tokenPayload = jwt_decode(this.token);
  }

  ngOnInit(): void {
    console.log('change-username ', this.dialogData.data);
    this.changeUsernameForm = this.formBuilder.group({
      name: [null, Validators.required],
    });
    this.changeUsernameForm.patchValue(this.dialogData.data); //quite important for accepting values into a dialog form
  }

  handleChangeUsernameRequest() {
    this.ngxService.start();
    var formData = this.changeUsernameForm.value;
    var data = {
      email: this.tokenPayload.sub,
      username: formData.name,
    };
    this.userService.changeUsername(data).subscribe((response: any) => {
      this.ngxService.stop();
      this.responseMessage = response?.message;
      this.dialogRef.close();
      this.snackbarService.openSnackBar(this.responseMessage, "success");
    }, (error) => {
      console.log(error);
      this.ngxService.stop();
      if(error.error?.message) {
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }
      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

}

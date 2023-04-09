import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { NgxUiLoaderService } from 'ngx-ui-loader';
import { SnackbarService } from '../services/snackbar.service';
import { UserService } from '../services/user.service';
import { GlobalConstants } from '../shared/global-constants';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: any = FormGroup;
  responseMessage: any;

  constructor(private formBuilder: FormBuilder,
    private router: Router,
    private userService: UserService,
    private dialogRef: MatDialogRef<LoginComponent>,
    private ngxService: NgxUiLoaderService,
    private snackbarService: SnackbarService) { }

  ngOnInit(): void {
    this.loginForm = this.formBuilder.group({
      email: [null, [Validators.required, Validators.pattern(GlobalConstants.emailRegex)]],
      password: [null, Validators.required]
    })
  }

  handleSubmit() {
    this.ngxService.start(); //start loader
    var formData = this.loginForm.value; //take the values from the loginform
    var data = {
      email: formData.email,
      password: formData.password
    }
    //passing DTO data to login method
    this.userService.login(data).subscribe((response: any) => {
      this.ngxService.stop(); //stop loader after receiving response
      this.dialogRef.close(); //close pop-up dialog
      localStorage.setItem('token', response.accessToken);
      this.router.navigate(['/cafe/dashboard']);
    }, (error) => {
      this.ngxService.stop(); //stop loader
      if(error.error?.message) {
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }

      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error); //pop up message for error
    })
  }

}

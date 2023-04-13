import {Component, EventEmitter, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CategoryService} from "../../../services/category.service";
import {SnackbarService} from "../../../services/snackbar.service";
import {GlobalConstants} from "../../../shared/global-constants";
import {UserService} from "../../../services/user.service";
import {RoleService} from "../../../services/role.service";

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss']
})
export class UserEditComponent implements OnInit {
  onUserEdit = new EventEmitter();
  userForm: any = FormGroup;
  dialogAction: any = 'Add';
  action: any = 'Add';
  responseMessage: any;
  roles: any = [];

  constructor(@Inject(MAT_DIALOG_DATA) public dialogData: any,
              private formBuilder: FormBuilder,
              private userService: UserService,
              private dialogRef: MatDialogRef<UserEditComponent>,
              private categoryService: CategoryService,
              private snackbarService: SnackbarService,
              private roleService: RoleService) { }

  ngOnInit(): void {
    this.userForm = this.formBuilder.group({
      name: [null, [Validators.required, Validators.pattern(GlobalConstants.nameRegex)]],
      email: [null, Validators.required],
      contactNumber: [null, Validators.required],
      roleId: [null, Validators.required]
    });

    if (this.dialogData.action === 'Edit') {
      this.dialogAction = 'Edit';
      this.action = 'Update';
      //display the value name when choosing edit option
      this.userForm.patchValue(this.dialogData.data);
    }

    this.getRoles();
  }

  getRoles () {
    this.roleService.getRoles().subscribe((response: any) => {
      this.roles = response;
    }, (error: any)=> {

      if(error.error?.message) {
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }

      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    })
  }

  handleSubmit() {
    if (this.dialogAction === 'Edit') {
      this.edit();
    }
  }

  edit() {
    var formData = this.userForm.value; //get the values from the form
    //DTO info, to be passed to backend
    var data = {
      id: this.dialogData.data.id,
      name: formData.name,
      email: formData.email,
      contactNumber: formData.contactNumber,
      roleId: formData.roleId
    };
    //passing DTO info to server API
    this.userService.edit(data).subscribe((response: any) => {
      this.dialogRef.close();
      this.onUserEdit.emit();
      this.responseMessage = response.message;
      this.snackbarService.openSnackBar(this.responseMessage, 'success'); //opens up pop-up message
    }, (error: any) => {

      if(error.error?.message) {
        this.responseMessage = error.error?.message;
      }else {
        this.responseMessage = GlobalConstants.genericError;
      }

      this.snackbarService.openSnackBar(this.responseMessage, GlobalConstants.error);
    });
  }

}

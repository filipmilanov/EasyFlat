import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {UserDetail} from '../../dtos/auth-request';
import {Observable} from "rxjs";
import {Router} from "@angular/router";
import {SharedFlat} from "../../dtos/sharedFlat";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {ToastrService} from "ngx-toastr";
import {NgbModal, NgbModalOptions} from '@ng-bootstrap/ng-bootstrap';
import {AdminSelectionModalComponent} from '../admin-selection-modal/admin-selection-modal.component';
import {LocalNgModuleData} from "@angular/compiler-cli/src/ngtsc/scope";

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  user: UserDetail;
  accountForm: FormGroup;
  submitted = false;
  error = false;
  errorMessage = '';



  users: UserDetail[];

  constructor(private modalService: NgbModal, private formBuilder: FormBuilder, private authService: AuthService, private sharedFlatService: SharedFlatService, private router: Router, private notification: ToastrService) {
    this.accountForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required]],
      flatName: [''],
      password: ['', [Validators.minLength(8)]],
      admin: ['']
    });

  }

  openAdminSelectionModal(): void {
    // Check if the user is an admin before opening the modal
    console.log(this.users);
    if (this.users.length == 0) {
      if (confirm('Are you sure you want to sign out?')) {
        this.signOut();
      }
    } else {
      if (this.user.admin) {
        const options: NgbModalOptions = {
          centered: true,
        };

        const modalRef = this.modalService.open(AdminSelectionModalComponent, options);
        modalRef.componentInstance.users = this.users;

        modalRef.result.then((selectedUserId) => {
          // Handle the selected user ID and update admin
          console.log('Selected User ID:', selectedUserId);
          this.authService.setAdmin(selectedUserId).subscribe({
            next: (result) => {
              console.log('Admin set successfully:', result);
            },
            error: (error) => {
              console.error('Error setting admin:', error);
            },
            complete: () => {
              console.log('Admin setting complete');
            }
          });

          console.log('Admin is set')

          // Show a confirmation dialog if you want
          if (confirm('Are you sure you want to sign out?')) {
            this.signOut();
          }
        });
      } else {
        // If the user is not an admin, directly proceed to sign out
        if (confirm('Are you sure you want to sign out?')) {
          this.signOut();
        }
      }
    }
  }


  ngOnInit(): void {
    // Fetch user data and update form values
    this.authService.getUser(this.authService.getToken()).subscribe(
      (user) => {
        this.user = user;
        console.log('User :', this.user);

        this.accountForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          email: this.user.email,
          flatName: this.user.flatName,
          admin: this.user.admin
        });
      },
      (error) => {
        console.error('Error fetching user:', error);
      }
    );
    // Fetch all users
    this.authService.getUsers(this.authService.getToken()).subscribe({
      next: (users) => {
        this.users = users;
      },
      error: (error) => {
        console.error('Error fetching users:', error);
      }
    });
  }

  update(): void {
    this.submitted = true;
    const formValue = this.accountForm.value;
    const password = formValue.password;

    if (password === '' || this.accountForm.get('password').untouched) {
      delete formValue.password;
    }
    console.log(this.accountForm.valid)

    if (this.accountForm.valid) {
      const userDetail: UserDetail = new UserDetail(this.user.id, this.accountForm.controls.firstName.value, this.accountForm.controls.lastName.value, this.accountForm.controls.email.value, null, this.accountForm.controls.password.value, this.accountForm.controls.admin.value);
      console.log(userDetail)
      this.authService.update(userDetail).subscribe({
        next: () => {
          console.log('Successfully updated user: ' + userDetail.email);
          this.notification.success('Successfully updated user: ' + userDetail.email)
        },
        error: error => {
          console.log('Could not update due to:');
          console.log(error);
          this.error = true;
          if (typeof error.error === 'object') {
            this.errorMessage = error.error.error;
          } else {
            this.errorMessage = error.error;
          }
        }
      });
    } else {
      console.log('Invalid input');
    }

    console.log(formValue)
  }

  vanishError() {
    this.error = false;
  }

  delete() {
    if (confirm("Are you sure you want to delete your account?")) {
      this.authService.delete(this.user).subscribe({
        next: (deletedUser: UserDetail) => {
          console.log('User deleted:', deletedUser);
          this.authService.logoutUser();
          this.router.navigate(['']);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }

  signOut() {
    this.authService.signOut(this.accountForm.controls.flatName.value, this.authService.getToken()).subscribe({
      next: () => {
        console.log('User signed out from flat: ' + this.user.flatName);
        this.router.navigate(['']);
      },
      error: error => {
        console.log('Could not sign out due to:');
        console.log(error);
        this.error = true;
        if (typeof error.error === 'object') {
          this.errorMessage = error.error.error;
        } else {
          this.errorMessage = error.error;
        }
      }
    });
  }


  deleteFlat() {
    if (confirm("Are you sure you want to delete the shared flat?")) {
      this.sharedFlatService.delete(this.user).subscribe({
        next: (deletedFlat: SharedFlat) => {
          console.log('Shared flat deleted from user :', deletedFlat);
          this.router.navigate(['']);
        },
        error: error => {
          console.error(error.message, error);
        }
      });
    }
  }
}


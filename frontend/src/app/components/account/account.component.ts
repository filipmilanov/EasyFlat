import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { UserDetail } from '../../dtos/auth-request';

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

  constructor(private formBuilder: FormBuilder, private authService: AuthService) {
    this.accountForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.minLength(8)]]
    });
  }

  ngOnInit(): void {
    // Fetch user data and update form values
    this.authService.getUser(this.authService.getToken()).subscribe(
      (user) => {
        this.user = user;
        console.log('User :', this.user);

        // Update form controls with fetched user data
        this.accountForm.patchValue({
          firstName: this.user.firstName,
          lastName: this.user.lastName,
          email: this.user.email
          // Password won't be prefilled for security reasons
        });
      },
      (error) => {
        console.error('Error fetching user:', error);
      }
    );
  }

  update(): void {
    this.submitted = true;
    const formValue = this.accountForm.value;
    const password = formValue.password;

    if (password === '' || this.accountForm.get('password').untouched) {
      delete formValue.password;
    }

    if (this.accountForm.valid) {
      const userDetail: UserDetail = new UserDetail(this.accountForm.controls.firstName.value,this.accountForm.controls.lastName.value,
        this.accountForm.controls.email.value, this.accountForm.controls.password.value);
      console.log(userDetail)
      this.authService.update(userDetail).subscribe({
        next: () => {
          console.log('Successfully updated user: ' + userDetail.email);
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
}


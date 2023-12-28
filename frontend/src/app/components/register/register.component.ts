import {Component, OnInit} from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import {AuthRequest, UserDetail} from '../../dtos/auth-request';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit{
  registerForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  registerUser() {
    this.submitted = true;
    if (this.registerForm.valid) {
      const userDetail: UserDetail = new UserDetail(null,this.registerForm.controls.firstName.value,this.registerForm.controls.lastName.value,  this.registerForm.controls.email.value,null, this.registerForm.controls.password.value,null);
      console.log(userDetail)
      this.authService.registerUser(userDetail).subscribe({
        next: () => {
          console.log('Successfully registered user: ' + userDetail.email);
          this.router.navigate(['']);
        },
        error: error => {
          console.log('Could not register due to:');
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
  }

  vanishError() {
    this.error = false;
  }

  ngOnInit(): void {
  }
}

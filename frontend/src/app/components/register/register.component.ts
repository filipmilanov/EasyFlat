import { Component } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { AuthRequest } from '../../dtos/auth-request';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';

  constructor(private formBuilder: UntypedFormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  registerUser() {
    this.submitted = true;
    if (this.registerForm.valid) {
      const authRequest: AuthRequest = new AuthRequest(this.registerForm.controls.email.value, this.registerForm.controls.password.value);
      // Pass the authRequest to the authentication service for user registration
      console.log(authRequest)
      this.authService.registerUser(authRequest).subscribe({
        next: () => {
          console.log('Successfully registered user: ' + authRequest.email);
          // After successful registration, you can redirect the user to the login page or any other desired route
          this.router.navigate(['/message']);
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
}

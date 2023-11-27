import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from "@angular/router";
import { SharedFlatService } from "../../services/sharedFlatService";
import { UntypedFormBuilder, UntypedFormGroup, Validators } from "@angular/forms";
import { AuthService } from "../../services/auth.service";

@Component({
  selector: 'app-login-shared-flat',
  templateUrl: './login-shared-flat.component.html',
  styleUrls: ['./login-shared-flat.component.scss']
})
export class LoginSharedFlatComponent implements OnInit {
  loginForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';

  constructor(
    private formBuilder: UntypedFormBuilder,
    private service: SharedFlatService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  loginInFlat(): void {
    this.submitted = true;

    if (this.loginForm.valid) {
      const name = this.loginForm.get('name')?.value;
      const password = this.loginForm.get('password')?.value;

      this.service.loginInFlat(name, password).subscribe(
        (response: any) => {
          console.log('Login successful!', response);
          this.router.navigate(['/dashboard']);
        },
        (error: any) => {
          console.error('Login failed!', error);
          this.error = true;
          this.errorMessage = 'Invalid credentials. Please try again.';
        }
      );
    }
  }

  createFlat(): void {
    const name = this.loginForm.get('name')?.value;
    const password = this.loginForm.get('password')?.value;

    this.service.createFlat(name, password).subscribe(
      (response: any) => {
        console.log('Flat created successfully!', response);
        this.router.navigate(['/dashboard']);
      },
      (error: any) => {
        console.error('Flat creation failed!', error);
        this.error = true;
        this.errorMessage = 'Failed to create flat. Please try again.';
      }
    );
  }

  ngOnInit(): void {}
}

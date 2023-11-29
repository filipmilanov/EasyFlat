import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {SharedFlat} from "../../dtos/sharedFlat";
import {UserDetail} from "../../dtos/auth-request";

@Component({
  selector: 'app-login-flat',
  templateUrl: './login-flat.component.html',
  styleUrls: ['./login-flat.component.scss']
})
export class LoginFlatComponent implements OnInit{
  loginForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';

  constructor(private formBuilder: UntypedFormBuilder, private sharedFlatService: SharedFlatService, private router: Router) {
    this.loginForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }
  loginWG(): void {
    this.submitted = true;
    if (this.loginForm.valid) {
      const sharedFlat : SharedFlat = new SharedFlat(this.loginForm.controls.name.value, this.loginForm.controls.password.value)
      this.authenticateWG(sharedFlat);
      console.log(sharedFlat);
    } else {
      console.log('Invalid input');
    }
  }

  ngOnInit(): void {
  }

  private authenticateWG(sharedFlat: SharedFlat) {
    console.log('Try to authenticate shared flat: ' + sharedFlat.name);
    this.sharedFlatService.loginWG(sharedFlat).subscribe({
      next: () => {
        this.router.navigate(['']);
      },
      error: (error) => {
        console.log('Could not log in due to:');
        console.log(error);
        this.error = true;
        if (error) {
          this.errorMessage = 'Invalid credentials. Could not log in.';
          // Navigate to /wgLogin
          this.router.navigate(['/wgLogin']);
        }
      }
    });
  }

  navigateToCreate() {
    this.router.navigate(['/wgCreate']); // Navigates to /wgCreate route
  }


  vanishError() {
    this.error = false;
  }
}

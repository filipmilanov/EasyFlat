import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {SharedFlat} from "../../dtos/sharedFlat";

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
      const sharedFlat : SharedFlat = new SharedFlat(this.loginForm.controls.flatName.value, this.loginForm.controls.password.value)
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
        console.log('Successfully logged in user: ' + sharedFlat.name);
        this.router.navigate(['']);
      },
      error: error => {
        console.log('Could not log in due to:');
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

  navigateToCreate() {
    this.router.navigate(['/wgCreate']); // Navigates to /wgCreate route
  }


  vanishError() {
    this.error = false;
  }
}

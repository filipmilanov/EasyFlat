import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {SharedFlat} from "../../dtos/sharedFlat";
import {UserDetail} from "../../dtos/auth-request";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-login-flat',
  templateUrl: './login-flat.component.html',
  styleUrls: ['./login-flat.component.scss']
})
export class LoginFlatComponent implements OnInit{
  user: UserDetail
  loginForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';


  constructor(private formBuilder: UntypedFormBuilder, private sharedFlatService: SharedFlatService,private authService: AuthService, private router: Router) {
    this.loginForm = this.formBuilder.group({
      flatName: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }
  loginWG(): void {
    this.submitted = true;
    if (this.loginForm.valid) {
      const sharedFlat: SharedFlat = new SharedFlat(
        this.loginForm.controls.flatName.value,
        this.loginForm.controls.password.value
      );
      console.log('Try to authenticate shared flat: ' + sharedFlat.name);
      this.sharedFlatService.loginWG(sharedFlat, this.authService.getToken()).subscribe(
        () => {
          console.log('You have successfully logged in!');
          this.router.navigate(['']); // Navigate on successful login
        },
        (error) => {
          console.log('Could not log in due to:');
          console.log(error);
          this.error = true;
          if (error) {
            this.errorMessage = 'Invalid credentials. Could not log in.';
            // Navigate to /wgLogin
            this.router.navigate(['/wgLogin']);
          }
        }
      );
    } else {
      console.log('Invalid input');
    }
  }

  ngOnInit(): void {
    this.authService.getUser(this.authService.getToken()).subscribe(
      (user) => {
        this.user = user;
        console.log('User :', this.user);
      },
      (error) => {
        console.error('Error fetching user:', error);
      }
    );
  }


  // private authenticateWG(sharedFlat: SharedFlat) {
  //   console.log('Try to authenticate shared flat: ' + sharedFlat.name);
  //   this.sharedFlatService.loginWG(sharedFlat).subscribe({
  //     next: () => {
  //       this.router.navigate(['']);
  //     },
  //     error: (error) => {
  //       console.log('Could not log in due to:');
  //       console.log(error);
  //       this.error = true;
  //       if (error) {
  //         this.errorMessage = 'Invalid credentials. Could not log in.';
  //         // Navigate to /wgLogin
  //         this.router.navigate(['/wgLogin']);
  //       }
  //     }
  //   });
  // }

  navigateToCreate() {
    this.router.navigate(['/wgCreate']); // Navigates to /wgCreate route
  }


  vanishError() {
    this.error = false;
  }

    delete() {
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

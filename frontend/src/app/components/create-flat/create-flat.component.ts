import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {Router} from "@angular/router";
import {SharedFlat} from "../../dtos/sharedFlat";
import {AuthService} from "../../services/auth.service";
import {ToastrService} from "ngx-toastr";

@Component({
  selector: 'app-create-flat',
  templateUrl: './create-flat.component.html',
  styleUrls: ['./create-flat.component.scss']
})
export class CreateFlatComponent implements OnInit{
  createForm: UntypedFormGroup;
  submitted = false;
  error = false;
  errorMessage = '';


  constructor(private formBuilder: UntypedFormBuilder, private notification: ToastrService, private sharedFlatService: SharedFlatService,private authService: AuthService, private router: Router) {
    this.createForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      repeatPassword: ['', [Validators.required, Validators.minLength(8)],],
      password: ['', [Validators.required, Validators.minLength(8)], ],
    });
  }

  createWG(): void{
    this.submitted = true;
    if (this.createForm.valid) {
      const sharedFlat : SharedFlat = new SharedFlat(this.createForm.controls.name.value, this.createForm.controls.password.value)
      console.log(sharedFlat);
      if (this.createForm.controls.password.value != this.createForm.controls.repeatPassword.value) {
        this.notification.error("Passwords didn't match")
      } else {
        this.sharedFlatService.createWG(sharedFlat, this.authService.getToken()).subscribe({
          next: () => {
            this.changeEventToTrue();
            this.router.navigate(['/account']);
            this.notification.success('Successfully created shared flat: ' + sharedFlat.name, "Success");
          },
          error: error => {
            let firstBracket = error.error.indexOf('[');
            let lastBracket = error.error.indexOf(']');
            let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
            let errorDescription = error.error.substring(0, firstBracket);
            errorMessages.forEach(message => {
              this.notification.error("Error creating shared flat", error)
            });
          }
        });
      }
    } else {
      console.log('Invalid input');
    }
  }
  ngOnInit(): void {
  }


  changeEventToTrue() {
    this.sharedFlatService.changeEvent();
  }

}

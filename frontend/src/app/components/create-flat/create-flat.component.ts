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
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  createWG(): void{
    this.submitted = true;
    if (this.createForm.valid) {
      const sharedFlat : SharedFlat = new SharedFlat(this.createForm.controls.name.value, this.createForm.controls.password.value)
      console.log(sharedFlat);
      this.sharedFlatService.createWG(sharedFlat, this.authService.getToken()).subscribe({
        next: () => {
          this.changeEventToTrue();
          this.router.navigate(['/account']);
          this.notification.success('Successfully created shared flat: ' + sharedFlat.name, "Success");
        },
        error: error => {
          this.error = true;
          if (typeof error.error === 'object') {
            this.errorMessage = error.error.error;
          } else {
            this.errorMessage = error.error;
          }
          this.notification.error("Could not create a shared flat due to: ", error);
        }
      });
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

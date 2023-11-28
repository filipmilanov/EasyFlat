import {Component, OnInit} from '@angular/core';
import {UntypedFormBuilder, UntypedFormGroup, Validators} from "@angular/forms";
import {SharedFlatService} from "../../services/sharedFlat.service";
import {Router} from "@angular/router";
import {SharedFlat} from "../../dtos/sharedFlat";

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

  constructor(private formBuilder: UntypedFormBuilder, private sharedFlatService: SharedFlatService, private router: Router) {
    this.createForm = this.formBuilder.group({
      name: ['', [Validators.required]],
      password: ['', [Validators.required, Validators.minLength(8)]]
    });
  }

  createWG(): void{
    this.submitted = true;
    if (this.createForm.valid) {
      const sharedFlat : SharedFlat = new SharedFlat(this.createForm.controls.flatName.value, this.createForm.controls.password.value)
      console.log(sharedFlat);
      this.sharedFlatService.createWG(sharedFlat).subscribe({
        next: () => {
          console.log('Successfully created shared flat: ' + sharedFlat.name);
          this.router.navigate(['/wgLogin']);
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
  ngOnInit(): void {
  }


}

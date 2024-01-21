import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {ChoresDto, ChoreSearchDto} from "../../../dtos/chores";
import {ToastrService} from "ngx-toastr";
import {ChoreService} from "../../../services/chore.service";
import {HttpResponse} from "@angular/common/http";
import {tap} from "rxjs/operators";

@Component({
  selector: 'app-all-chore',
  templateUrl: './all-chore.component.html',
  styleUrls: ['./all-chore.component.scss']
})
export class AllChoreComponent {
  chores: ChoresDto[];
  searchParams: ChoreSearchDto = {
    userName: '',
    endDate: null,
  };

  constructor(private router: Router,
              private choreService: ChoreService,
              private notification: ToastrService) {
  }

  navigateToNewChore() {
    this.router.navigate(['/chores', 'add']);
  }

  ngOnInit() {
    this.loadChores();
  }

  loadChores() {
    this.choreService.getChores(this.searchParams).subscribe({
      next: res => {
        this.chores = res.sort((a: ChoresDto, b: ChoresDto) => {
          return new Date(a.endDate).getTime() - new Date(b.endDate).getTime();
        });
      },
      error: error => {
        let firstBracket = error.error.indexOf('[');
        let lastBracket = error.error.indexOf(']');
        let errorMessages = error.error.substring(firstBracket + 1, lastBracket).split(',');
        let errorDescription = error.error.substring(0, firstBracket);
        errorMessages.forEach(message => {
          this.notification.error(message, errorDescription);
        });
      }
    });
  }

  assignChores() {
    this.choreService.assginChores().subscribe({
      next: res => {
        this.chores = res;
        this.notification.success("Successfully assigned chores")
        this.loadChores();
      },
      error: error => {
        this.notification.error("Created Chores are already assigned ")
      }
    });
  }

  exportPDF() {
    this.choreService.generateChoreListPDF().subscribe((response: HttpResponse<Blob>) => {
        const fileName = 'chores.pdf';

        const blob = new Blob([response.body], {type: 'application/pdf'});
        const downloadLink = document.createElement('a');
        downloadLink.href = window.URL.createObjectURL(blob);
        downloadLink.download = fileName;
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      },
      error => {
        this.notification.error("No chores found to export")
      });
  }

  navigateToMyChores() {
    this.router.navigate(['/chores/my']);
  }

  navigateToPreference() {
    this.router.navigate(['/chores/preference']);
  }

  navigateToLeaderboard() {
    this.router.navigate(['/chores/leaderboard']);
  }
}

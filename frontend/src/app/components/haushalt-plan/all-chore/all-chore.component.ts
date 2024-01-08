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
            error: err => {
                console.error("Error fetching chores", err);
                this.notification.error("Error loading chores")
            }
        })
    }

    assignChores() {
        this.choreService.assginChores().subscribe({
            next: res => {
                this.chores = res;
                this.notification.success("Successfully assigned chores")
            },
            error: err => {
                console.error("Error fetching chores with users")
                this.notification.error("Error loading chores")
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
                console.error("Error exporting PDF");
            });
    }
}

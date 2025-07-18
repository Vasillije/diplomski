import { Component } from '@angular/core';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-unblock-user-view',
  standalone: false,
  
  templateUrl: './unblock-user-view.component.html',
  styleUrl: './unblock-user-view.component.css'
})
export class UnblockUserViewComponent {
  dataSource : any;
  displayedColumns: string[] = ['id', 'username', 'name', 'lastname', 'email', 'penaltyPoints', 'unblock'];

  constructor(private adminService : AdminService){}

  ngOnInit(){
    this.getBlockedUsers();
  }

  getBlockedUsers(){
    this.adminService.getBlockedUsers().subscribe(
      (data) =>{
        this.dataSource = data;
        console.log(this.dataSource);
      },
      (error) =>
        alert("Error")
    );
  }

  unblock(element : any){
    this.adminService.unblockUser(element.id).subscribe(
    (data) =>{
        alert("User unblocked!")
        this.getBlockedUsers();
      },
      (error) =>
        alert("Error")
    );
  }
}

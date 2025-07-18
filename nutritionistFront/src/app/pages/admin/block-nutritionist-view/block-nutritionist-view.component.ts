import { Component } from '@angular/core';
import { AdminService } from '../services/admin.service';

@Component({
  selector: 'app-block-nutritionist-view',
  standalone: false,
  
  templateUrl: './block-nutritionist-view.component.html',
  styleUrl: './block-nutritionist-view.component.css'
})
export class BlockNutritionistViewComponent {
  dataSource : any;
  displayedColumns: string[] = ['id', 'username', 'name', 'lastname', 'email', 'penaltyPoints', 'block'];

  constructor(private adminService : AdminService){}

  ngOnInit(){
    this.getNutritionistsForBlock();
  }

  getNutritionistsForBlock(){
    this.adminService.getNutritionistsForBlock().subscribe(
      (data) => {
        this.dataSource = data;
        console.log(this.dataSource);
      },
      (error) =>
        alert(error.message)
    );
  }
  
  block(element : any) {
    this.adminService.blockNutritionist(element.id).subscribe(
      (data) => {
        alert("Nutritionist blocked");
        this.getNutritionistsForBlock();
      },
      (error) =>
        alert("Could not block nutritionist")
    );
  }
}

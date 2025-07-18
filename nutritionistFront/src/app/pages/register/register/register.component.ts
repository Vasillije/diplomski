import { Component } from '@angular/core';
import { RegisterService } from '../services/register.service';
import { RegisterDto } from '../interfaces/RegisterDto';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  
  constructor(private registerService : RegisterService, private router : Router){}
  
  register(result : any) {
    if(result.password !== result.retypedPassword){
      alert('Passwords dont match');
      return;
    }

    const dto : RegisterDto = {
      username: result.username,
      email: result.email,
      password: result.password
    };

    this.registerService.register(dto).subscribe(
      (data) =>{
        alert("Registration succedded");
        console.log(data);
        this.router.navigate(['']);
      },
      (error) =>
        alert("Could not register")
    );

  }
}

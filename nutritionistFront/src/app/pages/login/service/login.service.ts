import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginDto } from '../interfaces/LoginDto';
import { AuthResponseDto } from '../interfaces/AuthResponseDto';
import { Router } from '@angular/router';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class LoginService {
  public show: number = 0;
  private access_token = null;

  constructor(private http : HttpClient, private router : Router) { }

  login(dto : LoginDto){
    return this.http.post<AuthResponseDto>('/api/users/login', dto);
  }

  logout(){
    this.hasLoggedTrue(this.getUserId()!)
    localStorage.removeItem("JWT");
    this.userLoginTracker();
    this.router.navigate(['/login']);
  }

  tokenIsPresent() {
    return this.access_token != undefined && this.access_token != null;
  }

  getTokenInfo() : any{
    const jwt =localStorage.getItem('JWT');
    if(jwt != null){
      const tokenInfo = jwtDecode(jwt);
      //console.log(tokenInfo);
      return tokenInfo;
    }
  }

  getRole() : string | null {
    const tokenInfo = this.getTokenInfo();
    if (tokenInfo) {
      return tokenInfo['role'];

    }
    return null;
  }

  getUserId() : number | null {
    const tokenInfo = this.getTokenInfo();
    if (tokenInfo) {
      return tokenInfo['userId'];
    }
    return null;
  }

  getUsername() : string | null {
    const tokenInfo = this.getTokenInfo();
    if (tokenInfo) {
      return tokenInfo.sub;
    }
    return null;
  }

  getToken() {
    return this.access_token;
  }

  userLoginTracker() : Boolean{
    let logged= localStorage.getItem('JWT');
    if (logged){
       this.show =1;
       return true;
    }
    else {
     this.show = 0;
     return false;
    }
   }

   getUsersPenaltyPoints(id : number){
    return this.http.get<number>('/api/users/getUsersPenaltyPoints/'+id);
   }

   getHasLogged(userId : number){
    return this.http.get<Boolean>('api/users/getHasLogged/'+userId);
   }

   hasLoggedTrue(userId : number){
    return this.http.post<void>('api/users/hasLoggedTrue', userId);
   }
}

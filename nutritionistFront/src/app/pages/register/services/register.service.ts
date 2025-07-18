import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { RegisterDto } from '../interfaces/RegisterDto';

@Injectable({
  providedIn: 'root'
})
export class RegisterService {

  constructor(private http : HttpClient) { }

  register(dto : RegisterDto){
    return this.http.post<RegisterDto>('/api/users/register', dto);
  }

}

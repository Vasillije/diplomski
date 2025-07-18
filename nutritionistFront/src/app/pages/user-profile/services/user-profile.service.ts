import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserProfileDto } from '../interfaces/UserProfileDto';
import { GeneralInfoDto } from '../interfaces/GeneralInfoDto';
import { HealthInfoDto } from '../interfaces/HealthInfoDto';
import { ContactInfoDto } from '../interfaces/ContactInfoDto';
import { UserDto } from '../interfaces/UserDto';

@Injectable({
  providedIn: 'root'
})
export class UserProfileService {

  constructor(private http : HttpClient) { }

  getUserByUsername(username : string){
    return this.http.get<UserProfileDto>('/api/users/getByUsername/' + username);
  }

  changeGeneralInfo(dto : GeneralInfoDto){
    return this.http.post<GeneralInfoDto>('/api/users/changeGeneralInfo', dto);
  }

  changeHealthInfo(dto : HealthInfoDto){
    return this.http.post<HealthInfoDto>('/api/users/changeHealthInfo', dto);
  }

  changeContactInfo(dto : ContactInfoDto){
    return this.http.post<ContactInfoDto>('/api/users/changeContactInfo', dto);
  }

  getUserInfo(id : number){
    return this.http.get<UserDto>('/api/users/getUserInfo/' + id);
  }

}

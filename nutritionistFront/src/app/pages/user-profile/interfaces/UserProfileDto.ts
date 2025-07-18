export interface UserProfileDto{
  id: number;
  username: string;
  name: string;
  lastname: string;
  email: string;
  password: string;
  phone : string;
  address : string;
  gender: string;
  physicalActivity: string;
  weight: number;
  height: number;
  age: number;
  goal: string;
  penaltyPoints: number;
  diseases: Boolean;
  roles: string[];
}
export interface PatientAppointmentDto{
    appointmentId : number,
    date : Date,
    duration : number,
    nutritionistName : string,
    nutritionistLastName : string,
    hasComplaint : boolean
}
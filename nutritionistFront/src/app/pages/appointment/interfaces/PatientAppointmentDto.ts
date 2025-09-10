export interface PatientAppointmentDto{
    appointmentId : number,
    date : Date,
    duration : number,
    nutritionistFullName : string,
    hasComplaint : boolean,
    dateBooked : Date,
    nutritionistId : number,
    type : string
}
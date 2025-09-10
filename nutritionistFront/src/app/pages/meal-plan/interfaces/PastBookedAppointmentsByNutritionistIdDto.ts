export interface PastBookedAppointmentsByNutritionistIdDto{
    appointmentId : number,
    patientId : number,
    duration : number,
    date : Date,
    patientsFullName : string,
    dateBooked : Date
}
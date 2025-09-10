export interface AppointmentsByDateDto{
    appointmentId : number,
    date : Date,
    duration : number,
    patientName : string,
    patientLastName : string,
    formattedDate?: string,
    formattedDuration? : string
}
export interface NutritionistAppointmentDto{
    appointmentId : number,
    date : Date,
    duration : number,
    patientFullName : string,
    formattedDate?: string,
    formattedDuration? : string
}
export interface NutritionistComplaintsDto{
    id : number,
    appointmentId : number,
    nutritionistId : number,
    content : string
    patientsFullName : string,
    appointmentDate : Date,
    complaintDate : Date,
    formattedAppointmentDate? : string,
    formattedComplaintDate? : string
}
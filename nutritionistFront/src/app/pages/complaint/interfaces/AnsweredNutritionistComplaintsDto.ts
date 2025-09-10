export interface AnsweredNutritionistComplaintsDto{
    id : number,
    appointmentId : number,
    nutritionistId : number,
    content : string
    patientsFullName : string,
    appointmentDate : Date,
    answerContent : string,
    complaintDate : Date,
    answerDate : Date,
    formattedAppointmentDate? : string,
    formattedComplaintDate? : string,
    formattedAnswerDate? : string
}
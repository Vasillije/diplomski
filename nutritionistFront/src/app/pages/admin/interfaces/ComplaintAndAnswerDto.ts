export interface ComplaintAndAnswerDto{
    complaintId : number,
    complaintContent : string,
    answerId : number,
    answerContent : string,
    nutritionistId : number,
    patientId : number,
    appointmentId : number,
    nutritionistFullName : string,
    patientFullName : string,
    complaintDate : Date,
    answerDate : Date,
    appointmentDate : string
}
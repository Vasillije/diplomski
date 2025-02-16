package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.complaint.AnswerComplaintDto;
import com.sportscenter.nutritionist.dto.complaint.ComplaintAndAnswerDto;
import com.sportscenter.nutritionist.dto.complaint.CreateComplaintDto;
import com.sportscenter.nutritionist.dto.complaint.NutritionistComplaintsDto;

import java.util.List;

public interface ComplaintService {
    public void createComplaint(CreateComplaintDto dto);
    public List<NutritionistComplaintsDto> getComplaintsByNutritionistId(int nutritionistId);
    public void answerComplaint(AnswerComplaintDto dto);
    public List<ComplaintAndAnswerDto> getComplaintsAndAnswers();
}

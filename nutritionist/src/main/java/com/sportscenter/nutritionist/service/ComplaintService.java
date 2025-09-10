package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.complaint.*;

import java.util.List;

public interface ComplaintService {
    public void createComplaint(CreateComplaintDto dto);
    public List<NutritionistComplaintsDto> getComplaintsByNutritionistId(int nutritionistId);
    public void answerComplaint(AnswerComplaintDto dto);
    public List<ComplaintAndAnswerDto> getComplaintsAndAnswers();
    public List<AnsweredNutritionistComplaintsDto> getAnsweredComplaintsByNutritionistId(int nutritionistId);
    public ComplaintInfoDto getComplaintInfo(int id);
}

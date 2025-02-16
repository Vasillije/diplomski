package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.ComplaintAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<ComplaintAnswer, Integer> {

}

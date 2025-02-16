package com.sportscenter.nutritionist.repository;

import com.sportscenter.nutritionist.models.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintRepository extends JpaRepository<Complaint, Integer> {
    public Complaint findComplaintByAppointmentId(int id);
}

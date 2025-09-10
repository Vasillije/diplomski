package com.sportscenter.nutritionist.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date dateAndTime;
    private int duration;
    private int patientId;
    private int nutritionistId;
    private boolean isAvailable;
    @Column(nullable = true)
    private boolean hasComplaint = false;
    @Column(nullable = true)
    private boolean hasComplaintAnswer = false;
    @Column(nullable = true)
    private boolean hasMealPlan = false;
    @Column(nullable = true)
    private Date dateBooked;
    @Column(nullable = true)
    private String type;
    @Version
    private int version;
}

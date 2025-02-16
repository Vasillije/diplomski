package com.sportscenter.nutritionist.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int appointmentId;
    private int patientId;
    private int nutritionistId;
    private String content;
    @Column(nullable = true)
    private int answerId;
}

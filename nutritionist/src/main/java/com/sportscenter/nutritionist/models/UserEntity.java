package com.sportscenter.nutritionist.models;

import com.sportscenter.nutritionist.enums.Disease;
import com.sportscenter.nutritionist.enums.Gender;
import com.sportscenter.nutritionist.enums.Goal;
import com.sportscenter.nutritionist.enums.PhysicalActivity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String username;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private String phone;
    private String address;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Enumerated(EnumType.STRING)
    private PhysicalActivity physicalActivity;
    private Double weight;
    private Double height;
    private Integer age;
    @Enumerated(EnumType.STRING)
    private Goal goal;
    private Integer penaltyPoints;
    //@Enumerated(EnumType.STRING)
    private boolean disease;
    @ElementCollection
    private List<Integer> appointmentIds;
    @Column(nullable = true)
    private boolean hasLogged = false;
    @Column(nullable = true)
    private boolean isBlocked = false;
    @Column(nullable = true)
    private boolean hasPreliminaryPlan = false;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();
}

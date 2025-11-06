package com.sportscenter.nutritionist.service.impl;

import com.sportscenter.nutritionist.dto.users.*;
import com.sportscenter.nutritionist.enums.*;
import com.sportscenter.nutritionist.models.*;
import com.sportscenter.nutritionist.repository.*;
import com.sportscenter.nutritionist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AppointmentRepository appointmentRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AppointmentRepository appointmentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    public Optional<UserEntity> getByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public GeneralInfoDto changeGeneralInfo(GeneralInfoDto dto) {
        UserEntity user = userRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("User not found"));

        List<UserEntity> users = userRepository.findAll();
        users.remove(user);
        for (UserEntity u : users) {
            if (u.getUsername().equals(dto.getUsername())) {
                throw new IllegalArgumentException("User with this username already exists.");
            }
        }

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        if (!dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        user.setName(dto.getName());
        user.setLastname(dto.getLastName());
        userRepository.save(user);
        return dto;
    }

    @Override
    public HealthInfoDto changeHealthInfo(HealthInfoDto dto) {
        Gender gender = Gender.values()[dto.getGender()];
        PhysicalActivity activity = PhysicalActivity.values()[dto.getPhysicalActivity()];
        Goal goal = Goal.values()[dto.getGoal()];
        //Disease disease = Disease.values()[dto.getDiseases()];

        UserEntity user = userRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        user.setGender(gender);
        user.setPhysicalActivity(activity);
        user.setWeight(dto.getWeight());
        user.setHeight(dto.getHeight());
        user.setAge(dto.getAge());
        user.setGoal(goal);
        user.setDisease(dto.getDiseases());

        userRepository.save(user);
        return dto;
    }

    @Override
    public ContactInfoDto changeContactInfo(ContactInfoDto dto) {
        UserEntity user = userRepository.findById(dto.getId()).orElseThrow(() -> new RuntimeException("User not found"));
        List<UserEntity> users = userRepository.findAll();
        users.remove(user);

        for (UserEntity u : users) {
            if (u.getEmail().equals(dto.getEmail())) {
                throw new IllegalArgumentException("Email that you changed is already registered.");
            }
        }

        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhoneNumber());
        user.setAddress(dto.getAddress());
        userRepository.save(user);
        return dto;
    }

    @Override
    public int getUsersPenaltyPoints(int id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getPenaltyPoints();
    }

    @Override
    public Boolean getIsBlocked(int id){
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return user.isBlocked();
    }

    @Override
    public Boolean getHasLogged(int userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return user.isHasLogged();
    }

    @Override
    public void hasLoggedTrue(int userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setHasLogged(true);
        userRepository.save(user);
    }

    @Override
    public List<NutritionistsForBlockDto> nutritionistsForBlock() {
        List<UserEntity> allUsers = userRepository.findAll();
        List<UserEntity> nutritionistsForBlock = new ArrayList<>();
        List<NutritionistsForBlockDto> dtos = new ArrayList<>();

        for (UserEntity u : allUsers) {
            if (u.getRoles().get(0).getName().equals("NUTRITIONIST") && u.getPenaltyPoints() >= 5 && !u.isBlocked()) {
                nutritionistsForBlock.add(u);
            }
        }

        for (UserEntity u : nutritionistsForBlock) {
            NutritionistsForBlockDto dto = new NutritionistsForBlockDto();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setName(u.getName());
            dto.setLastName(u.getLastname());
            dto.setEmail(u.getEmail());
            dto.setPenaltyPoints(u.getPenaltyPoints());

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void blockNutritionist(int id) {
        UserEntity nutritionist = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        nutritionist.setBlocked(true);
        userRepository.save(nutritionist);
    }

    @Override
    public List<UnblockUserDto> getBlockedUsers() {
        List<UserEntity> allUsers = userRepository.findAll();
        List<UserEntity> blockedUsers = new ArrayList<>();
        List<UnblockUserDto> dtos = new ArrayList<>();

        for (UserEntity u : allUsers) {
            if (u.isBlocked()) {
                blockedUsers.add(u);
            }
        }

        for (UserEntity u : blockedUsers) {
            UnblockUserDto dto = new UnblockUserDto();
            dto.setId(u.getId());
            dto.setUsername(u.getUsername());
            dto.setName(u.getName());
            dto.setLastName(u.getLastname());
            dto.setEmail(u.getEmail());
            dto.setPenaltyPoints(u.getPenaltyPoints());

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public void unblockUser(int id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setPenaltyPoints(0);
        user.setBlocked(false);
        userRepository.save(user);
    }

    @Override
    public UserDto getUserInfo(int id) {
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        UserDto dto = new UserDto();

        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setPassword(user.getPassword());
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setGender(user.getGender());
        dto.setPhysicalActivity(user.getPhysicalActivity());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setAge(user.getAge());
        dto.setGoal(user.getGoal());
        dto.setDisease(user.isDisease());

        return dto;
    }

    public UserInfoDto getUserInfoByAppointmentId(int id){
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new RuntimeException("Appointment not found"));
        UserEntity patient = userRepository.findById(appointment.getPatientId()).orElseThrow(() -> new RuntimeException("User not found"));

        UserInfoDto dto = new UserInfoDto();
        dto.setPatientId(patient.getId());
        dto.setGender(patient.getGender().ordinal());
        dto.setPhysicalActivity(patient.getPhysicalActivity().ordinal());
        dto.setWeight(patient.getWeight());
        dto.setHeight(patient.getHeight());
        dto.setAge(patient.getAge());
        dto.setGoal(patient.getGoal().ordinal());
        dto.setDiseases(patient.isDisease());
        dto.setUsername(patient.getUsername());

        return dto;
    }

    public PatientInfoDto getPatientsInfo(int id){
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        PatientInfoDto dto = new PatientInfoDto();

        dto.setPatientId(user.getId());
        dto.setGender(user.getGender());
        dto.setPhysicalActivity(user.getPhysicalActivity());
        dto.setWeight(user.getWeight());
        dto.setHeight(user.getHeight());
        dto.setAge(user.getAge());
        dto.setGoal(user.getGoal());

        return dto;
    }

    public boolean hasPreliminaryNutritionPlan(int id){
        UserEntity user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        return user.isHasPreliminaryPlan();
    }

    public List<PatientFullNameDto> getPatientFullNames(){
        List<UserEntity> users = userRepository.findAllPatients();
        List<PatientFullNameDto> dtos = new ArrayList<>();

        for(UserEntity u : users){
            String fullName = u.getName() + ' ' + u.getLastname();
            PatientFullNameDto dto = new PatientFullNameDto(u.getId(), u.getName() + ' ' + u.getLastname());
            dtos.add(dto);
        }
        return dtos;
    }

}

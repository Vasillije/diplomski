package com.sportscenter.nutritionist.service;

import com.sportscenter.nutritionist.dto.users.*;
import com.sportscenter.nutritionist.models.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public Optional<UserEntity> getByUsername(String username);
    public GeneralInfoDto changeGeneralInfo(GeneralInfoDto dto);
    public HealthInfoDto changeHealthInfo(HealthInfoDto dto);
    public ContactInfoDto changeContactInfo(ContactInfoDto dto);
    public int getUsersPenaltyPoints(int id);
    public Boolean getHasLogged(int userId);
    public void hasLoggedTrue(int userId);
    public List<NutritionistsForBlockDto> nutritionistsForBlock();
    public void blockNutritionist(int id);
    public List<UnblockUserDto> getBlockedUsers();
    public void unblockUser(int id);
    public UserDto getUserInfo(int id);
    public UserHealthInfoDto getUserHealthInfoByAppointmentId(int id);
}

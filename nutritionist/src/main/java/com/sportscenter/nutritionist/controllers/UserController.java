package com.sportscenter.nutritionist.controllers;

import com.sportscenter.nutritionist.dto.users.*;
import com.sportscenter.nutritionist.enums.Disease;
import com.sportscenter.nutritionist.enums.Gender;
import com.sportscenter.nutritionist.enums.Goal;
import com.sportscenter.nutritionist.enums.PhysicalActivity;
import com.sportscenter.nutritionist.models.Role;
import com.sportscenter.nutritionist.models.UserEntity;
import com.sportscenter.nutritionist.repository.RoleRepository;
import com.sportscenter.nutritionist.repository.UserRepository;
import com.sportscenter.nutritionist.security.JWTGenerator;
import com.sportscenter.nutritionist.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private AuthenticationManager authanticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;
    private UserService userService;

    @Autowired
    public UserController(AuthenticationManager authanticationManager, UserRepository userRepository,
                          RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator, UserService userService) {
        this.authanticationManager = authanticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.userService = userService;
    }

    @PostMapping("register")
    public ResponseEntity<RegisterDto> register(@RequestBody RegisterDto registerDto) {
        if(userRepository.existsByUsername(registerDto.getUsername())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        UserEntity user = new UserEntity();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setName("");
        user.setLastname("");
        user.setPhone("");
        user.setAddress("");
        user.setWeight(0.0);
        user.setHeight(0.0);
        user.setAge(0);
        user.setPenaltyPoints(0);
        user.setDisease(false);
        user.setGender(Gender.EMPTY);
        user.setGoal(Goal.EMPTY);
        user.setPhysicalActivity(PhysicalActivity.EMPTY);
        user.setBlocked(false);

        Role roles = roleRepository.findByName("NUTRITIONIST").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);
        return new ResponseEntity<>(registerDto, HttpStatus.OK);
    }

    @PostMapping("login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        Authentication authentication = authanticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);
        return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
    }

    @GetMapping("getByUsername/{username}")
    public ResponseEntity<?> getById(@PathVariable String username){
        return new ResponseEntity<>(userService.getByUsername(username), HttpStatus.OK);
    }

    @PostMapping("changeGeneralInfo")
    public ResponseEntity<?> changeGeneralInfo(@RequestBody GeneralInfoDto dto){
        return new ResponseEntity<>(userService.changeGeneralInfo(dto) ,HttpStatus.OK);
    }

    @PostMapping("changeHealthInfo")
    public ResponseEntity<?> changeHealthInfo(@RequestBody HealthInfoDto dto){
            return new ResponseEntity<>(userService.changeHealthInfo(dto) ,HttpStatus.OK);
    }

    @PostMapping("changeContactInfo")
    public ResponseEntity<?> changeContactInfo(@RequestBody ContactInfoDto dto){
        return new ResponseEntity<>(userService.changeContactInfo(dto) ,HttpStatus.OK);
    }

    @GetMapping("getUsersPenaltyPoints/{id}")
    public ResponseEntity<?> getUsersPenaltyPoints(@PathVariable int id){
        return new ResponseEntity<>(userService.getUsersPenaltyPoints(id) ,HttpStatus.OK);
    }

    @GetMapping("getIsBlocked/{id}")
    public ResponseEntity<Boolean> getIsBlocked(@PathVariable int id){
        return new ResponseEntity<Boolean>(userService.getIsBlocked(id) ,HttpStatus.OK);
    }

    @PostMapping("getHasLogged")
    public ResponseEntity<Boolean> getHasLogged(@RequestBody int userId){
        return new ResponseEntity<>(userService.getHasLogged(userId) ,HttpStatus.OK);
    }

    @GetMapping("hasLoggedTrue/{userId}")
    public ResponseEntity<?> hasLoggedTrue(@PathVariable int userId){
        userService.hasLoggedTrue(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getNutritionistsForBlock")
    public ResponseEntity<List<NutritionistsForBlockDto>> getNutritionistsForBlock(){
        return new ResponseEntity<>(userService.nutritionistsForBlock(), HttpStatus.OK);
    }

    @PostMapping("blockNutritionist")
    public ResponseEntity<Void> blockNutritionist(@RequestBody int id){
        userService.blockNutritionist(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getBlockedUsers")
    public ResponseEntity<List<UnblockUserDto>> getBlockedUsers(){
        return new ResponseEntity<>(userService.getBlockedUsers(), HttpStatus.OK);
    }

    @PostMapping("unblockUser")
    public ResponseEntity<Void> unblockUser(@RequestBody int id){
        userService.unblockUser(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("getUserInfo/{id}")
    public ResponseEntity<UserDto> getUserInfo(@PathVariable int id){
        return new ResponseEntity<>(userService.getUserInfo(id), HttpStatus.OK);
    }

    @GetMapping("getUserHealthInfoByAppointmentId/{id}")
    public ResponseEntity<UserHealthInfoDto> getUserHealthInfoByAppointmentId(@PathVariable int id){
        return new ResponseEntity<>(userService.getUserHealthInfoByAppointmentId(id), HttpStatus.OK);
    }
}

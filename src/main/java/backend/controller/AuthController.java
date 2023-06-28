package backend.controller;


import backend.config.Constant;
import backend.dto.request.SignInDTO;
import backend.dto.request.SignUpDTO;
import backend.dto.response.JwtResponse;
import backend.dto.response.ResponseMessage;
import backend.model.Role;
import backend.model.User;
import backend.model.enums.RoleName;
import backend.security.jwt.JwtProvider;
import backend.security.userprincipal.UserPrinciple;
import backend.service.role.IRoleService;
import backend.service.user.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = {"/api/auth"})
@CrossOrigin(origins = "*")
public class AuthController {
    private static final ResponseMessage responseMessage = ResponseMessage.getInstance();
    //Sign Up
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    //Sign In
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/sign-up")
    public ResponseEntity<?> register(
        @Valid
        @RequestBody
        SignUpDTO signUpDTO,
        BindingResult result) {
        userService.generateDefaultValueDatabase();
        if (result.hasErrors()) {
            responseMessage.setMessage(Constant.SIGN_UP_FORM_INVALID);
            System.err.println(result.getFieldError());
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (signUpDTO.checkAllNull()) {
            responseMessage.setMessage(Constant.ALL_FIELD_NULL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (userService.existsByUserName(signUpDTO.getUserName())) {
            responseMessage.setMessage(Constant.USER_NAME_EXIST);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (userService.existsByEmail(signUpDTO.getEmail())) {
            responseMessage.setMessage(Constant.USER_EMAIL_EXIST);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        User user = new User();
        user.setName(signUpDTO.getName());
        user.setUserName(signUpDTO.getUserName());
        user.setEmail(signUpDTO.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDTO.getPassword()));
        Set<String> strRoles = signUpDTO.getRoles();
        Set<Role> roles = new HashSet<>();
        strRoles.forEach(role -> {
            switch (role.toUpperCase()) {
                case "ADMIN":
                    Role adminRole = roleService.findByRoleName(RoleName.ADMIN).orElseThrow(
                        () -> new RuntimeException(Constant.ROLE_NOT_FOUND));
                    roles.add(adminRole);
                    break;
                case "PM":
                    Role pmRole = roleService.findByRoleName(RoleName.PM).orElseThrow(
                        () -> new RuntimeException(Constant.ROLE_NOT_FOUND));
                    roles.add(pmRole);
                    break;
                default:
                    Role userRole = roleService.findByRoleName(RoleName.USER).orElseThrow(
                        () -> new RuntimeException(Constant.ROLE_NOT_FOUND));
                    roles.add(userRole);
            }
        });
        user.setRoles(roles);
        userService.save(user);
        responseMessage.setMessage(Constant.RESISTER_SUCCESS);
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> login(
        @Valid
        @RequestBody
        SignInDTO signInForm,
        BindingResult result) {
        userService.generateDefaultValueDatabase();
        if (result.hasErrors()) {
            responseMessage.setMessage(Constant.SIGN_IN_FORM_INVALID);
            System.err.println(result.getFieldError());
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (signInForm.checkAllNull()) {
            responseMessage.setMessage(Constant.ALL_FIELD_NULL);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        Optional<User> findUser = userService.findByUserName(signInForm.getUserName());
        if (!findUser.isPresent()) {
            responseMessage.setMessage(Constant.ACCOUNT_NOT_EXIST);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }
        if (findUser.get().getStatus()) {
            responseMessage.setMessage(Constant.ACCOUNT_BLOCK);
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);
        }

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(signInForm.getUserName(), signInForm.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtProvider.creatToken(authentication);
        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        JwtResponse jwtResponse = new JwtResponse(token, userPrinciple.getName(), userPrinciple.getAvatar(),
            userPrinciple.getAuthorities());
        return ResponseEntity.ok(jwtResponse);
    }
}

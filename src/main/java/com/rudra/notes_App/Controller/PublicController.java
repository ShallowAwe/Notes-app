package com.rudra.notes_App.Controller;

import com.rudra.notes_App.Exception.ErrorResponse;
import com.rudra.notes_App.Model.LoginRequest;
import com.rudra.notes_App.Services.NotesServices;
import com.rudra.notes_App.Services.UserDetailServiceImpl;
import com.rudra.notes_App.Services.UserServices;
import com.rudra.notes_App.Model.UserModel;
import com.rudra.notes_App.Util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/public")
public class PublicController {

    final NotesServices notesServices;
    final UserServices userServices;
    final AuthenticationManager authenticationManager;
    final UserDetailsService userDetailService;
    final JwtUtil jwtUtil;
    @Autowired
    public PublicController(JwtUtil jwtUtil , NotesServices notesServices, UserServices userServices, UserDetailsService userDetailsService, AuthenticationManager authenticationManager) {
        this.notesServices = notesServices;
        this.userServices = userServices;
        this.authenticationManager =authenticationManager;
        this.userDetailService =userDetailsService;
        this.jwtUtil =jwtUtil;
    }


    @PostMapping("/login_jwt")
    public ResponseEntity<?> loginViaJwt(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);



            final UserDetails userDetails = userDetailService.loadUserByUsername(loginRequest.getUsername());

            final String jwt = jwtUtil.generateToken(userDetails.getUsername());

            return ResponseEntity.ok(new HashMap<String, String>() {{
                put("token", jwt);
            }});

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<UserModel> saveUSer(@RequestBody UserModel user) {
        try {

            userServices.createNewUSer(user);
            return new ResponseEntity<>(user, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

}

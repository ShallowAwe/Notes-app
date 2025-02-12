package com.rudra.notes_App.Controller;

import com.rudra.notes_App.Services.NotesServices;
import com.rudra.notes_App.Services.UserServices;
import com.rudra.notes_App.Model.NotesModel;
import com.rudra.notes_App.Model.UserModel;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.beans.Encoder;
import java.util.List;


@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    final NotesServices notesServices;
    final  UserServices userServices;
      @Autowired
    public UserController(NotesServices notesServices, UserServices userServices) {
        this.notesServices = notesServices;
        this.userServices = userServices;
    }






//    @PostMapping
//    public ResponseEntity<UserModel> saveUSer(@RequestBody UserModel user) {
//        try {
//
//            userServices.createNewUSer(user);
//            return new ResponseEntity<>(user, HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//    }

    @GetMapping
    public ResponseEntity<?> getAllNotes() {
        List<UserModel> users = userServices.getAllUsers();
        if (users == null || users.isEmpty()) {
            return new ResponseEntity<>("No users found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }


    @PostMapping("/{id}/notes")
    public ResponseEntity<?> saveNoteOnUser(@PathVariable ObjectId id, @RequestBody NotesModel notes) {


        NotesModel savedNote = notesServices.savenote(notes);
        userServices.addNoteToUser(id, savedNote);
        return ResponseEntity.ok(savedNote);

    }
    @GetMapping("/{username}/notes")
    public ResponseEntity<?> fetchUserNotes(@PathVariable String username) {
        try {
            // get authenticated username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();

           //check if the given authenticated username matched the username in path variable
            if (username.equals(authenticatedUser)) {
                List<NotesModel> userNotes = userServices.getUserNotesByUserName(username);
                if (userNotes == null || userNotes.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No notes found for the user.");
                }
                return ResponseEntity.ok(userNotes);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
        } catch (Exception e) {

            logger.error("Error fetching notes for user: " + username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching notes.");
        }
    }

    @GetMapping("/{id}/notesById")
    public ResponseEntity<?> fetchUserNotesById(@PathVariable ObjectId id) {

        try {

            List<NotesModel> userNotes = userServices.getUserNotes(id);

            if (userNotes == null || userNotes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No notes found for the user.");
            }
            return ResponseEntity.ok(userNotes);

        } catch (Exception e) {

            logger.error("Error fetching notes for user ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while fetching notes.");
        }
    }


}

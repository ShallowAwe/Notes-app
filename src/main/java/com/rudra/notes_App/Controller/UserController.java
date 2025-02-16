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
import org.springframework.web.server.ResponseStatusException;


import java.beans.Encoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


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


    @PostMapping("id/{id}/notes")
    public ResponseEntity<?> saveNoteOnUser(@PathVariable ObjectId id, @RequestBody NotesModel notes) {


        NotesModel savedNote = notesServices.savenote(notes);
        userServices.addNoteToUser(id, savedNote);
        return ResponseEntity.ok(savedNote);

    }
    @PostMapping("/{username}/savenote")
    public ResponseEntity<?> SaveNoteOnUser(@PathVariable String username, @RequestBody NotesModel notes){
        NotesModel savenote = notesServices.savenote(notes);
        userServices.addNoteByUsername(username,savenote);
        return  ResponseEntity.ok(savenote);
    }


    @GetMapping("/{username}/notesFetch")
    public ResponseEntity<?> fetchUserNotes(@PathVariable String username) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();


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
    @PutMapping("/updateNote/{username}")
    public ResponseEntity<?> updateNoteByUsername(
            @PathVariable String username,
            @RequestBody NotesModel noteDetails) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();

            if (!username.equals(authenticatedUser)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }

            // Fetch user and their notes
            UserModel user = userServices.getByUsername(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Find the specific note by ID in the user's notes
            NotesModel existingNote = user.getNotes().stream()
                    .filter(note -> note.getId().equals(noteDetails.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingNote == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found");
            }


            existingNote.setTitle(noteDetails.getTitle());
            existingNote.setContent(noteDetails.getContent());
            existingNote.setCreatedAt(LocalDateTime.now());

            notesServices.savenote(existingNote);

            userServices.saveUser(user);

            return ResponseEntity.ok("Note updated successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating note");
        }
    }
    @DeleteMapping("deleteNote/{username}/{id}")
    public ResponseEntity<?> deleteNoteByUsername(@PathVariable String username, @PathVariable ObjectId id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUser = authentication.getName();

        if (!username.equals(authenticatedUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this note.");
        }

        UserModel user = userServices.getUserByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean removed = user.getNotes().removeIf(note -> note.getId().equals(id));

        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found.");
        }

        // Save the updated user (assuming notes are stored inside the user model)
        userServices.saveUser(user);
        notesServices.DeleteNote(id);

        return ResponseEntity.ok("Note deleted successfully.");
    }

    @GetMapping("/{username}/NotesFetch")
    public ResponseEntity<?> fetchusernotes(@PathVariable String username) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUser = authentication.getName();

            if (!username.equals(authenticatedUser)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied.");
            }

            List<NotesModel> userNotes = userServices.getUserNotesByUserName(username);

            if (userNotes == null || userNotes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No notes found for the user.");
            }

            // Return the list of notes (id will be a string due to @JsonGetter in NotesModel)
            return ResponseEntity.ok(userNotes);
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

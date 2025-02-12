package com.rudra.notes_App.Services;

import com.rudra.notes_App.Exception.UserNotFound;
import com.rudra.notes_App.Repository.NotesRepo;
import com.rudra.notes_App.Model.NotesModel;
import com.rudra.notes_App.Model.UserModel;
import com.rudra.notes_App.Repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserServices {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private NotesRepo notesRepo;
    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    // Create
    public UserModel createNewUSer(UserModel user) {
        // Password encoding
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Saving user: {}", user);
        userRepo.save(user);
        return user;
    }
    public UserModel findByUsername(String username) {
        return userRepo.findByUsername(username
        );
    }
    // Read operations
    public Optional<UserModel> getUserById(ObjectId id) {
        return userRepo.findById(id);
    }

    public UserModel getUserByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public Optional<UserModel> getUserByEmail(String email) {
        return userRepo.findByMail(email);
    }

    public List<UserModel> getAllUsers() {
        return userRepo.findAll();
    }

    // Update
    public UserModel updateUser(UserModel user) {
        return userRepo.save(user);
    }

    // Delete
    public void deleteUser(ObjectId id) {
        userRepo.deleteById(id);
    }

    // Note management
    public void addNoteToUser(ObjectId userId, NotesModel note) {
        getUserById(userId).ifPresent(user -> {
            // Add only the note's ObjectId to the user's notes list
            user.getNotes().add(note);
            userRepo.save(user);
        });
    }



    public List<NotesModel> getUserNotes(ObjectId id) {
        UserModel user = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return user.getNotes();
    }
    public List<NotesModel> getUserNotesByUserName(String username) {

        UserModel user = userRepo.findByUsername(username);


        if (user != null) {
            return user.getNotes();
        } else {
            throw new UserNotFound("User not found");
        }
    }


    public void removeNoteFromUser(ObjectId userId, ObjectId noteId) {
        getUserById(userId).ifPresent(user -> {
            user.getNotes().remove(noteId);
            userRepo.save(user);
        });
    }

    //   Validation methods
    public boolean isUsernameAvailable(String username) {
        return !userRepo.existsByUsername(username);
    }

    public boolean isEmailAvailable(String email) {
        return !userRepo.existsByMail(email);
    }
}
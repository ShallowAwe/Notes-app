package com.rudra.notes_App.Controller;

import com.rudra.notes_App.Services.NotesServices;
import com.rudra.notes_App.Model.NotesModel;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/note")
public class NotesController {

    @Autowired
    private NotesServices notesServices;

    @PostMapping
    @Transactional
    public ResponseEntity<NotesModel> saveNote(@RequestBody NotesModel myNote) {

        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (myNote.getCreatedAt() == null) {
                myNote.setCreatedAt(LocalDateTime.now());
            }
            NotesModel savedNote = notesServices.savenote(myNote);
            return new ResponseEntity<>(savedNote, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }


    @GetMapping
    public ResponseEntity<?> getallNotes() {
        List<NotesModel> notes = notesServices.showall();
        if (notes != null && notes.isEmpty()) {
            return new ResponseEntity<>(notes, HttpStatus.OK);
        }
        return new ResponseEntity<>(notes, HttpStatus.NOT_FOUND);
    }



    @PutMapping("/{id}")
    public ResponseEntity<NotesModel> updateNote(@PathVariable ObjectId id, @RequestBody NotesModel note) {

        try {
            NotesModel updatedNote  = notesServices.updateNote(id, note);
            if (updatedNote != null){
                return new ResponseEntity<>(updatedNote, HttpStatus.OK);
            }
           return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

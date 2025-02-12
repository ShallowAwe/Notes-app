package com.rudra.notes_App.Services;

import com.rudra.notes_App.Model.NotesModel;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rudra.notes_App.Repository.NotesRepo;

import java.util.List;
import java.util.Optional;

@Service
public class NotesServices {

    @Autowired
    private NotesRepo notesRepo;


    public NotesModel savenote(NotesModel note) {
        return notesRepo.save(note);
    }


    public List<NotesModel> showall() {
        return notesRepo.findAll();
    }

    public Optional<NotesModel> findbyId(ObjectId id) {
        return notesRepo.findById(id);

    }

    public NotesModel updateNote(ObjectId id, NotesModel noteDetail) {
        return notesRepo.findById(id).map(existingNote -> {
            existingNote.setTitle(noteDetail.getTitle());
            existingNote.setContent(noteDetail.getContent());
            return notesRepo.save(existingNote);
        }).orElse(null);


    }


}

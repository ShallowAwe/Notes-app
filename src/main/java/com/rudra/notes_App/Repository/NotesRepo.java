package com.rudra.notes_App.Repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.rudra.notes_App.Model.NotesModel;
public interface NotesRepo extends MongoRepository<NotesModel , ObjectId>{

}

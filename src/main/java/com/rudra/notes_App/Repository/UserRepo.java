package com.rudra.notes_App.Repository;

import com.rudra.notes_App.Model.UserModel;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepo extends MongoRepository<UserModel, ObjectId> {
    Optional<UserModel> findByUsername(String username);
    Optional<UserModel> findByEmail(String email);

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

}

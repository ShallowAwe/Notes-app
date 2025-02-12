package com.rudra.notes_App.Model;

import jakarta.annotation.Generated;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection =  "notes_entries")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotesModel {

     @Id

     private ObjectId id;

     @NonNull
     private String title;

     private  String content;

     private LocalDateTime createdAt = LocalDateTime.now();

}

package com.brain.history.multimedia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author firecode16
 */
@Getter
@Setter
@Document(collection = "backdrops")
public class Backdrops {
    @Id
    @JsonIgnore
    private ObjectId _id;
    private Long userId;
    private String fileName;
    private Binary backdropImage;
}

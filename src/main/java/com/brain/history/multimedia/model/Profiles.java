package com.brain.history.multimedia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author firecode16
 */
@Getter
@Setter
@Document(collection = "profiles")
public class Profiles {
    @Id
    @JsonIgnore
    private ObjectId _id;
    private Long userId;
    private Long collectionId;
    private Long phone;
    private boolean auth;
    private String userName;
    private String fullName;
    private String email;
    private String postDate;
    private String overview;
    private Integer array;
    private int countContacts;
}

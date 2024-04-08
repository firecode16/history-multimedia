package com.brain.history.multimedia.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author firecode16
 */
@Getter
@Setter
public class PostedContent {

    private String _id;
    private Long collectionId;
    private String contentType;
    private String multimediaName;
    private Long share;
    private Long likes;
    private List<Comments> comments = new ArrayList<>();
}

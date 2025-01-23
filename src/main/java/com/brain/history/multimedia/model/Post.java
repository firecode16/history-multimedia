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
public class Post {
    private Long collectionId;
    private Long fileId;
    private String filePath;
    private List<Comments> comments = new ArrayList<>();
    private String multimediaName;
    private String mimeType;
    private String type;
    private Long share;
    private Long likes;
    private Long size;
}

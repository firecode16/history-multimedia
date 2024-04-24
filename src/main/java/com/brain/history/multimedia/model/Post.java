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

    private Long share;
    private List<Comments> comments = new ArrayList<>();
    private Long likes;
    private boolean image;
    private boolean video;
    private boolean audio;
    private boolean textPlain;
    private String multimediaName;
    private String contentType;
    private Long size;
}

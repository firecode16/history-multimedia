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
public class PostView {
    private Long collectionId;
    private String postDate;
    private String overview;
    private Integer array;
    private List<PostedContent> content = new ArrayList<>();

    public List<PostedContent> add(PostedContent postedContent) {
        content.add(postedContent);
        return content;
    }
}

package com.brain.history.multimedia.model;

import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author firecode16
 */
@Getter
@Setter
@EqualsAndHashCode
public class ProfilesView {
    private Long userId;
    private boolean isAuth;
    private String userName;
    private String fullName;
    private String email;
    private String backdropName;
    private int countContacts;
    private List<PostView> post = new ArrayList<>();

    public ProfilesView() {
    }

    public ProfilesView(Long userId, boolean isAuth, String userName, String fullName, String email, String backdropName, int countContacts) {
        this.userId = userId;
        this.isAuth = isAuth;
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.backdropName = backdropName;
        this.countContacts = countContacts;
    }

    public List<PostView> add(PostView postView) {
        post.add(postView);
        return post;
    }
}

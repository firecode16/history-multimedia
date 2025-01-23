package com.brain.history.multimedia.controller;

import com.brain.history.multimedia.model.Post;
import com.brain.history.multimedia.model.Profiles;
import com.brain.history.multimedia.response.MultimediaResponse;
import com.brain.history.multimedia.service.PostsService;
import static com.brain.history.multimedia.util.Util.ERROR_RESPONSE;
import static com.brain.history.multimedia.util.Util.OK_RESPONSE;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author firecode16
 */
@RestController
@CrossOrigin
public class PostsController {
    @Autowired
    private PostsService postsService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Gson gson;

    private MultimediaResponse response;

    @PostMapping("/save/posts")
    public MultimediaResponse save(@RequestParam Map<String, Object> map, @RequestParam("file") List<MultipartFile> listFile) throws IOException {
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Profiles profile = objectMapper.convertValue(map, Profiles.class);
        profile.setPostDate(LocalDateTime.now().toString());
        profile.setArray(listFile.size());

        String strPost = (String) map.get("post");
        Post[] posts = gson.fromJson(strPost, Post[].class);

        int result = postsService.save(profile, posts, listFile);

        if (result == 1) {
            response = new MultimediaResponse(OK_RESPONSE, result);
        } else {
            response = new MultimediaResponse(ERROR_RESPONSE, result);
        }
        return response;
    }
}

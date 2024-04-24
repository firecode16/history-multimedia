package com.brain.history.multimedia.controller;

import com.brain.history.multimedia.model.ProfilesView;
import com.brain.history.multimedia.response.HistoryMultimediaResponse;
import com.brain.history.multimedia.service.HistoryMultimediaService;
import static com.brain.history.multimedia.util.Util.ERROR_RESPONSE;
import static com.brain.history.multimedia.util.Util.OK_RESPONSE;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author firecode16
 */
@RestController
@CrossOrigin
public class HistoryMultimediaController {

    @Autowired
    private HistoryMultimediaService historyMultimediaService;
    private HistoryMultimediaResponse response;

    @GetMapping("/allPost")
    public HistoryMultimediaResponse getAllPostByUserId(@RequestParam Long userId, @RequestParam int page, @RequestParam int size) {
        final ProfilesView profilesView = historyMultimediaService.getAllPostByUserId(userId, page, size);
        return new HistoryMultimediaResponse(OK_RESPONSE, profilesView);
    }

    @GetMapping(value = "/backdrop/{userId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] getImageById(@PathVariable Long userId) {
        byte[] data = historyMultimediaService.getImageById(userId);
        return data;
    }

    @GetMapping(value = "/multimedia/{streamId}")
    public void getMultimediaById(@PathVariable ObjectId streamId, HttpServletResponse servletResponse) throws IOException {
        try (InputStream streamMedia = historyMultimediaService.getMultimediaById(streamId)) {
            servletResponse.flushBuffer();
            FileCopyUtils.copy(streamMedia, servletResponse.getOutputStream());
            streamMedia.close();
        }
    }

    @PostMapping("/poster/save")
    public HistoryMultimediaResponse save(@RequestBody Map<String, Object> map) throws IOException {
        int result = historyMultimediaService.save(map);

        if (result == 1) {
            response = new HistoryMultimediaResponse(OK_RESPONSE, result);
        } else {
            response = new HistoryMultimediaResponse(ERROR_RESPONSE, result);
        }
        return response;
    }
}

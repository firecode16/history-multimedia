package com.brain.history.multimedia.controller;

import com.brain.history.multimedia.model.ProfilesView;
import com.brain.history.multimedia.response.MultimediaResponse;
import com.brain.history.multimedia.service.HistoryMultimediaService;
import static com.brain.history.multimedia.util.Util.OK_RESPONSE;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private static final Logger logger = Logger.getLogger(HistoryMultimediaController.class.getName());

    @GetMapping("/allPost")
    public MultimediaResponse getAllPostByUserId(@RequestParam Long userId, @RequestParam int page, @RequestParam int size) {
        final ProfilesView profilesView = historyMultimediaService.getAllPostByUserId(userId, page, size);
        return new MultimediaResponse(OK_RESPONSE, profilesView);
    }

    @GetMapping(value = "/backdrop/{userId}", produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] getImageById(@PathVariable Long userId) {
        byte[] data = historyMultimediaService.getImageById(userId);
        return data;
    }

    @GetMapping(value = "/multimedia/{fileId}")
    public void getMultimediaById(@PathVariable String fileId, HttpServletResponse servletResponse) {
        try (ServletOutputStream out = servletResponse.getOutputStream()) {
            InputStream streamMedia = historyMultimediaService.getMultimediaById(fileId);
            out.write(streamMedia.readAllBytes());
        } catch (IOException ex) {
            if (ex.getMessage().contains("Connection reset by peer")) {
                logger.warning("Client disconnected before response was fully sent.");
            } else if (ex.getMessage().contains("An established connection was aborted")) {
                logger.warning("Handle client disconnect scenario.");
            } else {
                logger.log(Level.WARNING, ex.getMessage());
            }
        }
    }
}

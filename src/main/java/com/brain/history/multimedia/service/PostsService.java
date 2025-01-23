package com.brain.history.multimedia.service;

import com.brain.history.multimedia.model.Backdrops;
import com.brain.history.multimedia.model.Post;
import com.brain.history.multimedia.model.Profiles;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author firecode16
 */
@Service
public class PostsService {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;

    private File file;
    private InputStream inputStream;
    private DBObject postMetaData;
    private int result = 0;
    private final String backdropImage = "C:\\Users\\Fredi\\Documents\\Multimedia\\images\\backdropProfile.png";

    @Transactional
    public int save(Profiles profile, Post[] posts, List<MultipartFile> listFile) throws IOException {
        int count = 0;
        Backdrops backdrop = new Backdrops();

        mongoTemplate.save(profile, "profiles");
        Backdrops existBackdrop = mongoTemplate.findOne(new Query(Criteria.where("userId").is(profile.getUserId())), Backdrops.class, "backdrops");

        if (existBackdrop == null) {
            file = new File(backdropImage);
            backdrop.setUserId(profile.getUserId());
            backdrop.setFileName(file.getName());
            backdrop.setBackdropImage(new Binary(FileUtils.readFileToByteArray(file)));

            mongoTemplate.save(backdrop, "backdrops");
        }

        for (Post post : posts) {
            var partFile = listFile.get(count);
            post.setMultimediaName(partFile.getOriginalFilename());
            post.setSize(partFile.getSize());

            byte[] data = partFile.getBytes();
            inputStream = new ByteArrayInputStream(data);

            postMetaData = new BasicDBObject();
            postMetaData.put("userId", profile.getUserId());
            postMetaData.put("collectionId", profile.getCollectionId());
            postMetaData.put("userName", profile.getUserName());
            postMetaData.put("fullName", profile.getFullName());
            postMetaData.put("email", profile.getEmail());
            postMetaData.put("fileId", post.getFileId());
            postMetaData.put("filePath", post.getFilePath());
            postMetaData.put("share", post.getShare());
            postMetaData.put("likes", post.getLikes());
            postMetaData.put("comments", post.getComments());
            postMetaData.put("contentType", post.getMimeType());
            postMetaData.put("type", post.getType());
            postMetaData.put("multimediName", post.getMultimediaName());

            gridFsTemplate.store(inputStream, partFile.getOriginalFilename(), post.getMimeType(), postMetaData);
            count++;
            inputStream.close();
            partFile.getInputStream().close();
        }

        result = 1;
        return result;
    }

}

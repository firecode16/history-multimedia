package com.brain.history.multimedia.service;

import com.brain.history.multimedia.model.Backdrops;
import com.brain.history.multimedia.model.Post;
import com.brain.history.multimedia.model.PostView;
import com.brain.history.multimedia.model.PostedContent;
import com.brain.history.multimedia.model.Profiles;
import com.brain.history.multimedia.model.ProfilesView;
import com.brain.history.multimedia.repository.PostRepository;
import static com.brain.history.multimedia.util.Util.MATCH_JPG;
import static com.brain.history.multimedia.util.Util.MATCH_VIDEO;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.groupingBy;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author firecode16
 */
@Service
public class HistoryMultimediaService {

    @Autowired
    private PostRepository postRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private GridFsTemplate gridFsTemplate;
    @Autowired
    private GridFsOperations gridFsOperations;

    private GridFSFile gridFSFile;
    private GridFSFindIterable gridFSFindIterable;
    private Pageable pageable;
    private InputStream inputStream;
    private File file;
    private Query query;
    private DBObject postMetaData;
    private int result = 0;
    private ProfilesView profilesView;
    private PostView postView;
    private PostedContent postedContent;

    @Transactional
    public ProfilesView getAllPostByUserId(Long userId, int page, int size) {
        pageable = PageRequest.of(page, size);
        profilesView = new ProfilesView();
        postView = new PostView();

        query = new Query(Criteria.where("userId").is(userId)).with(pageable);
        List<Profiles> listProfiles = mongoTemplate.find(query, Profiles.class, "profiles");

        Backdrops backdrop = mongoTemplate.findOne(query, Backdrops.class, "backdrops");

        if (!listProfiles.isEmpty() & backdrop != null) {
            final Map<ProfilesView, List<Profiles>> mapProfilesView = listProfiles.stream().collect(
                    groupingBy(view -> new ProfilesView(view.getUserId(), view.isAuth(), view.getUserName(), view.getFullName(), view.getEmail(), "/".concat(backdrop.getFileName()), view.getCountContacts()))
            );

            mapProfilesView.entrySet().forEach((var mapEntry) -> {
                profilesView = mapEntry.getKey();
                mapEntry.getValue().forEach(profile -> {
                    postView = new PostView();
                    postView.setCollectionId(profile.getCollectionId());
                    postView.setPostDate(profile.getPostDate());
                    postView.setOverview(profile.getOverview());
                    postView.setArray(profile.getArray());
                    profilesView.add(postView);
                });
            });

            query = new Query(Criteria.where("metadata.userId").is(userId)).with(pageable);
            gridFSFindIterable = gridFsTemplate.find(query);

            profilesView.getPost().forEach((var post) -> {
                gridFSFindIterable.forEach((var gdFile) -> {
                    try {
                        final ObjectId objId = gdFile.getId().asObjectId().getValue();
                        final Document metadata = gdFile.getMetadata();
                        final LinkedHashMap<String, Object> linkedMap = new LinkedHashMap<>(metadata);
                        final Long collectionId = (Long) linkedMap.get("collectionId");
                        final String contentType = (String) linkedMap.get("contentType");
                        final String fileName = (String) linkedMap.get("multimediName");
                        final Long share = (Long) linkedMap.get("share");
                        final Long likes = (Long) linkedMap.get("likes");

                        if (post.getCollectionId().equals(collectionId)) {
                            postedContent = new PostedContent();
                            postedContent.set_id(objId.toString());
                            postedContent.setCollectionId(collectionId);
                            postedContent.setContentType(contentType);
                            postedContent.setMultimediaName(fileName);
                            postedContent.setShare(share);
                            postedContent.setLikes(likes);
                            post.getContent().add(postedContent);
                        }
                    } catch (IllegalStateException ex) {
                        ex.getLocalizedMessage();
                    }
                });
            });
        }
        return profilesView;
    }

    @Transactional(readOnly = true)
    public byte[] getImageById(Long userId) {
        Backdrops backdrop = postRepository.findByUserId(userId);
        return backdrop.getBackdropImage().getData();
    }

    @Transactional(readOnly = true)
    public InputStream getMultimediaById(ObjectId streamId) throws IOException {
        query = new Query(Criteria.where("_id").is(streamId));
        gridFSFile = gridFsTemplate.findOne(query);
        inputStream = gridFsOperations.getResource(gridFSFile).getInputStream();
        return inputStream;
    }

    @Transactional
    public int save(Map<String, Object> map) throws IOException {
        Profiles profile = new Profiles();
        Backdrops backdrop = new Backdrops();
        profile.setUserId(((Number) map.get("userId")).longValue());
        String strCollectionId = String.valueOf(((List<LinkedHashMap<String, String>>) map.get("post")).stream().findFirst().get().get("collectionId"));
        profile.setCollectionId(Long.valueOf(strCollectionId));
        profile.setAuth(true);
        profile.setUserName((String) map.get("userName"));
        profile.setFullName((String) map.get("fullName"));
        profile.setEmail((String) map.get("email"));
        profile.setPostDate(LocalDateTime.now().toString());
        profile.setOverview((String) map.get("overview"));
        file = new File((String) map.get("backdropImage"));
        profile.setArray(((List<LinkedHashMap<String, String>>) map.get("post")).stream().filter((var post) -> !post.containsKey("collectionId")).count());
        profile.setCountContacts(15);

        mongoTemplate.save(profile, "profiles");

        final Backdrops existBackdrop = mongoTemplate.findOne(new Query(Criteria.where("userId").is(profile.getUserId())), Backdrops.class, "backdrops");
        if (existBackdrop == null) {
            backdrop.setUserId(profile.getUserId());
            backdrop.setFileName(file.getName());
            backdrop.setBackdropImage(new Binary(FileUtils.readFileToByteArray(file)));
            mongoTemplate.save(backdrop, "backdrops");
        }

        List<LinkedHashMap<String, String>> linkedPost = (List<LinkedHashMap<String, String>>) map.get("post");
        String postId = String.valueOf(linkedPost.stream().findFirst().get().get("collectionId"));
        Long collectionId = Long.valueOf(postId);
        linkedPost.stream().filter((var model) -> !model.containsKey("collectionId")).forEach((var linked) -> {
            try {
                Post post = new Post();
                String filePath = linked.get("filePath");
                String share = String.valueOf(linked.get("share"));
                String likes = String.valueOf(linked.get("likes"));
                file = new File(filePath);

                if (filePath.endsWith(MATCH_JPG)) {
                    post.setImage(true);
                    post.setVideo(false);
                    post.setTextPlain(false);
                    post.setContentType("image/jpg");
                } else if (filePath.endsWith(MATCH_VIDEO)) {
                    post.setImage(false);
                    post.setVideo(true);
                    post.setTextPlain(false);
                    post.setContentType("video/mp4");
                }

                byte[] data = FileUtils.readFileToByteArray(file);
                inputStream = new ByteArrayInputStream(data);
                post.setShare(Long.valueOf(share));
                post.setLikes(Long.valueOf(likes));
                post.setMultimediaName(file.getName());
                post.setSize(FileUtils.sizeOf(file));

                postMetaData = new BasicDBObject();
                postMetaData.put("userId", profile.getUserId());
                postMetaData.put("collectionId", collectionId);
                postMetaData.put("userName", profile.getUserName());
                postMetaData.put("email", profile.getEmail());
                postMetaData.put("share", post.getShare());
                postMetaData.put("likes", post.getLikes());
                postMetaData.put("comments", post.getComments());
                postMetaData.put("contentType", post.getContentType());
                postMetaData.put("multimediName", post.getMultimediaName());

                gridFsTemplate.store(inputStream, file.getName(), post.getContentType(), postMetaData);
            } catch (IOException ex) {
                result = -1;
            }
        });
        result = 1;
        return result;
    }
}

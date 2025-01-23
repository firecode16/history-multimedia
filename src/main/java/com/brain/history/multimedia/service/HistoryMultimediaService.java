package com.brain.history.multimedia.service;

import com.brain.history.multimedia.model.Backdrops;
import com.brain.history.multimedia.model.PostView;
import com.brain.history.multimedia.model.PostedContent;
import com.brain.history.multimedia.model.Profiles;
import com.brain.history.multimedia.model.ProfilesView;
import com.brain.history.multimedia.repository.PostRepository;
import com.mongodb.MongoGridFSException;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.stream.Collectors.groupingBy;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    private GridFSFindIterable gridFSFindIterable;
    private Pageable pageable;
    private ProfilesView profilesView;
    private PostView postView;
    private PostedContent postedContent;
    
    private static final Logger logger = Logger.getLogger(HistoryMultimediaService.class.getName());

    @Transactional
    public ProfilesView getAllPostByUserId(Long userId, int page, int size) {
        pageable = PageRequest.of(page, size).withSort(Sort.by("postDate").descending());
        profilesView = new ProfilesView();
        postView = new PostView();

        Query query = Query.query(Criteria.where("userId").is(userId)).with(pageable);
        List<Profiles> listProfiles = mongoTemplate.find(query, Profiles.class, "profiles");

        Backdrops backdrop = mongoTemplate.findOne(query, Backdrops.class, "backdrops");

        if (!listProfiles.isEmpty()) {
            final Map<ProfilesView, List<Profiles>> mapProfilesView = listProfiles.stream().collect(
                    groupingBy(view -> new ProfilesView(view.getUserId(), view.isAuth(), view.getUserName(), view.getFullName(), view.getEmail(), "/".concat("backdropProfile.png"), view.getCountContacts()))
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

            query = Query.query(Criteria.where("metadata.userId").is(userId)).with(pageable);
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
    public InputStream getMultimediaById(String fileId) {
        InputStream inputStream = null;
        try {
            GridFSFile findFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId.trim())));
            inputStream = gridFsOperations.getResource(findFile).getInputStream();
        } catch (IOException | IllegalStateException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        } catch (MongoGridFSException e) {
            logger.log(Level.SEVERE, "File chunk error{0}: ", e.getLocalizedMessage());
        }
        return inputStream;
    }
}

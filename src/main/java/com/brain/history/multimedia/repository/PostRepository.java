package com.brain.history.multimedia.repository;

import com.brain.history.multimedia.model.Backdrops;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 *
 * @author firecode16
 */
public interface PostRepository extends MongoRepository<Backdrops, Long> {

    Backdrops findByUserId(Long userId);
}

package com.tam.profile.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tam.profile.entity.UserProfile;

@Repository
public interface UserProfileRepository extends MongoRepository<UserProfile, String> {
    Optional<UserProfile> findByUserId(String userId);
    //    List<UserProfile> findAllByUsernameLike(String username);
    Optional<UserProfile> findByContactInfoEmail(String email);

    Optional<UserProfile> findByContactInfoEmailOrContactInfoPhoneNumber(String searchString);
}

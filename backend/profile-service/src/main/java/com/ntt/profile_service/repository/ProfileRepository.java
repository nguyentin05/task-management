package com.ntt.profile_service.repository;

import com.ntt.profile_service.domain.Profile;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends Neo4jRepository<Profile, String> {
    Optional<Profile> findByUserId(String userId);
    boolean existsByUserId(String userId);
    List<Profile> findByUserIdIn(List<String> userIds);
}

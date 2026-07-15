package com.Address.demo.repositry;

import com.Address.demo.model.UserActivity;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserActivityRepository
        extends MongoRepository<UserActivity,String> {

    Optional<UserActivity> findByUsernameAndStatus(
            String username,
            String status
    );

    UserActivity findTopByUsernameOrderByLoginTimeDesc(
            String username
    );

    UserActivity findTopByUsernameAndStatusOrderByLoginTimeDesc(
            String username,
            String status
    );
}
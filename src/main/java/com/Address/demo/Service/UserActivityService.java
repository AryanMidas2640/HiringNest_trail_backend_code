package com.Address.demo.Service;

import com.Address.demo.model.UserActivity;
import com.Address.demo.repositry.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserActivityService {

    private final UserActivityRepository repo;

    public UserActivityService(UserActivityRepository repo) {
        this.repo = repo;
    }

    // LOGIN TRACK
    public void userLogin(String username,String role){

        UserActivity a = new UserActivity();

        a.setUsername(username);
        a.setRole(role);
        a.setStatus("ACTIVE");
        a.setLoginTime(LocalDateTime.now().toString());
        a.setLogoutTime("-");

        repo.save(a);
    }

    // LOGOUT TRACK
    public void userLogout(String username){

        UserActivity a =
                repo.findByUsernameAndStatus(
                        username,"ACTIVE"
                ).orElse(null);

        if(a != null){

            a.setStatus("OFFLINE");
            a.setLogoutTime(
                    LocalDateTime.now().toString()
            );

            repo.save(a);
        }
    }

    public List<UserActivity> getAllUsers(){
        return repo.findAll();
    }
}
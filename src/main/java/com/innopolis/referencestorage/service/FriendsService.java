package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.Friends;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.FriendsRepo;
import org.springframework.stereotype.Service;

@Service("friendsService")
public class FriendsService {

    UserService userService;

    FriendsRepo friendsRepo;

    public FriendsService(UserService userService, FriendsRepo friendsRepo) {
        this.userService = userService;
        this.friendsRepo = friendsRepo;
    }

    public User findUserByUid(Long uid) {
        return userService.findUserByUid(uid);
    }

    public Friends addFriends(Long ownerUid, Long friendUid) {
        Friends friends = new Friends(ownerUid, friendUid);
        return friendsRepo.save(friends);
    }
}

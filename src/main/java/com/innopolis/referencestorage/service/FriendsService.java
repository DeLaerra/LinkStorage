package com.innopolis.referencestorage.service;

import com.innopolis.referencestorage.domain.Friends;
import com.innopolis.referencestorage.domain.User;
import com.innopolis.referencestorage.repos.FriendsRepo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<Friends> findAllByFriend(long friend) {
        return friendsRepo.findAllByFriend(friend);
    }

    public List<Friends> findAllByOwner(long owner) {
        return friendsRepo.findAllByOwner(owner);
    }

    public List<User> showAllFriends(User user) {
        List<User> friends = new ArrayList<>();

        friendsRepo.findAllByOwner(user.getUid()).forEach(friend -> friends.add(userService.findUserByUid(friend.getFriend())));
        friendsRepo.findAllByFriend(user.getUid()).forEach(friend -> friends.add(userService.findUserByUid(friend.getOwner())));

        return friends;
    }

    public Friends addFriends(Long ownerUid, Long friendUid) {
        Friends friends = new Friends(ownerUid, friendUid);
        return friendsRepo.save(friends);
    }

    public boolean checkFriendship(User user, User friend) {
        boolean y = friendsRepo.existsByOwnerAndFriendEquals(user.getUid(), friend.getUid());
        boolean y2 = friendsRepo.existsByOwnerAndFriendEquals(friend.getUid(), user.getUid());
        return (friendsRepo.existsByOwnerAndFriendEquals(user.getUid(), friend.getUid()))
                || (friendsRepo.existsByOwnerAndFriendEquals(friend.getUid(), user.getUid()));
    }
}

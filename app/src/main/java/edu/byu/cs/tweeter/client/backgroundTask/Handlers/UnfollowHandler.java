package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class UnfollowHandler extends Handler {
    private final FollowService followService;
    private FollowService.MainObserver observer;
    private User user;

    public UnfollowHandler(FollowService followService, FollowService.MainObserver observer, User user) {
        this.followService = followService;
        this.observer = observer;
        this.user = user;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(UnfollowTask.SUCCESS_KEY);
        if (success) {
            followService.updateSelectedUserFollowingAndFollowers(user, observer);
            observer.setFollowingButton(false);
        } else if (msg.getData().containsKey(UnfollowTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(UnfollowTask.MESSAGE_KEY);
            observer.displayErrorMessage("Failed to unfollow: " + message);
        } else if (msg.getData().containsKey(UnfollowTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(UnfollowTask.EXCEPTION_KEY);
            observer.displayException(ex);
        }
    }
}
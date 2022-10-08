package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowHandler extends Handler {

    private final FollowService followService;
    private FollowService.GetFollowerUserObserver observer;
    private User user;
    private FollowService.MainObserver observerMain;

    public FollowHandler(FollowService followService, FollowService.GetFollowerUserObserver observer, User user, FollowService.MainObserver observerMain) {
        this.followService = followService;
        this.observer = observer;
        this.user = user;
        this.observerMain = observerMain;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
        if (success) {
            followService.updateSelectedUserFollowingAndFollowers(user, observerMain);
            observer.setFollowingButton(true);
        } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
            observer.displayErrorMessage("Failed to follow: " + message);
        } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
            observer.displayException(ex);
        }
    }
}
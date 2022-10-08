package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowHandler extends Handler {


    private FollowService.GetFollowerUserObserver observer;


    public FollowHandler(FollowService.GetFollowerUserObserver observer) {
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(FollowTask.SUCCESS_KEY);
        if (success) {
            observer.handleSuccess();
        } else if (msg.getData().containsKey(FollowTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(FollowTask.MESSAGE_KEY);
            observer.displayErrorMessage("Failed to follow: " + message);
        } else if (msg.getData().containsKey(FollowTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(FollowTask.EXCEPTION_KEY);
            observer.displayException(ex);
        }
    }
}

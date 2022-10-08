package edu.byu.cs.tweeter.client.backgroundTask.Handlers;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.service.FollowService;

public class GetFollowingCountHandler extends Handler {

    private FollowService.GetCountFollowObserver observer;

    public GetFollowingCountHandler(FollowService.GetCountFollowObserver observer) {
        this.observer = observer;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        boolean success = msg.getData().getBoolean(GetFollowingCountTask.SUCCESS_KEY);
        if (success) {
            int count = msg.getData().getInt(GetFollowingCountTask.COUNT_KEY);
            observer.setFollowingText(String.valueOf(count));
        } else if (msg.getData().containsKey(GetFollowingCountTask.MESSAGE_KEY)) {
            String message = msg.getData().getString(GetFollowingCountTask.MESSAGE_KEY);
            observer.displayErrorMessage("Failed to get following count: " + message);
        } else if (msg.getData().containsKey(GetFollowingCountTask.EXCEPTION_KEY)) {
            Exception ex = (Exception) msg.getData().getSerializable(GetFollowingCountTask.EXCEPTION_KEY);
            observer.displayException(ex);
        }
    }
}

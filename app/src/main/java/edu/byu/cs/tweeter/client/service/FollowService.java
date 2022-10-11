package edu.byu.cs.tweeter.client.service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.FollowCountHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.FollowerHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.GetUserHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.PagedNotificationHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.SimpleNotificationHandler;
import edu.byu.cs.tweeter.client.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowCountObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService {

    public interface GetFollowingObserver {
        void handleSuccess(List<User> followers, boolean hasMorePages);
        void handleFailure(String message);
        void handleException(Exception ex);
    }

    public interface GetFollowerObserver {
        void setIntent(User user);
        void displayErrorMessage(String message);
        void displayException(Exception ex);
        void addItems(List<User> followers, boolean hasMorePages);
    }

    public interface GetFeedObserver{
        void setIntent(User user);
        void displayErrorMessage(String message);
        void displayException(Exception ex);
        void addFeedItems(List<Status> feeds, boolean hasMorePages);
    }

    public interface GetUserObserver{
        void displayErrorMessage(String message);
        void displayException(Exception ex);
        void setIntent(User user);
    }

    public interface IsFollowerObserver{
        void displayException(Exception ex);
        void setFollowingButton(boolean isFollowed);
        void displayErrorMessage(String message);
    }

    public interface GetCountFollowObserver{
        void setFollowerText(String text);
        void setFollowingText(String text);
        void displayErrorMessage(String message);
        void displayException(Exception ex);
    }


    public void loadMoreItems(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedNotificationHandler<User>(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowingTask);
    }

    public void loadMoreItemsFollowers(AuthToken currUserAuthToken, User user, int pageSize, User lastFollowee, PagedObserver<User> observer) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(currUserAuthToken,
                user, pageSize, lastFollowee, new PagedNotificationHandler<>(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

    public void loadMoreItemsFeed(AuthToken currUserAuthToken, User user, int pageSize, Status lastStatus, PagedObserver<Status> observer){
        GetFeedTask getFeedTask = new GetFeedTask(currUserAuthToken,
                user, pageSize, lastStatus, new PagedNotificationHandler<>(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFeedTask);
    }

    public void getUser(String username, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                username, new GetUserHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getUserTask);
    }


    public void getFollowStatus(User user, FollowerObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), user, new FollowerHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(isFollowerTask);
    }

    public void followUser(User selectedUser, SimpleNotifyObserver observer){
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleNotificationHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(followTask);

    }

    public void unfollowUser(User selectedUser, SimpleNotifyObserver observer){
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new SimpleNotificationHandler(observer));
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(unfollowTask);
    }

    public void updateSelectedUserFollowingAndFollowers(User selectedUser, FollowCountObserver observer) {
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // Get count of most recently selected user's followers.
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowCountHandler(observer));
        executor.execute(followersCountTask);

        // Get count of most recently selected user's followees (who they are following)
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                selectedUser, new FollowCountHandler(observer));
        executor.execute(followingCountTask);
    }
}
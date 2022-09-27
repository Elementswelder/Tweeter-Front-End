package edu.byu.cs.tweeter.client.presenter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.view.main.followers.FollowersFragment;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresentor {

    private View view;
    private FollowService followService;
    private static final int PAGE_SIZE = 10;

    private boolean isLoading = false;
    private User lastFollower;
    private boolean hasMorePages;

    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void setIntent(User user);
    }

    private class GetFollowerObserver implements FollowService.GetFollowerObserver{

        @Override
        public void setIntent(User user) {
            view.setIntent(user);
        }

        @Override
        public void displayErrorMessage(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);

        }

        @Override
        public void displayException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);

        }

        @Override
        public void addItems(List<User> followers) {

        }
    }

    public FollowerPresentor(View view){
        this.view = view;
        followService = new FollowService();
    }

    public void loadMoreFollowers(String username) {
        followService.loadMoreFollowersFollower(username, new FollowerPresentor.GetFollowerObserver());
        view.displayMessage("Getting user's profile...Followers");
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void getMoreFollowers(User user){
        isLoading = true;
        view.setLoadingFooter(true);
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastFollower, new FollowersFragment.FollowersRecyclerViewAdapter.GetFollowersHandler());
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(getFollowersTask);
    }

}

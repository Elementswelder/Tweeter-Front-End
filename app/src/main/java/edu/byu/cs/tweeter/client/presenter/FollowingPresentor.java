package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresentor {

    private static final int PAGE_SIZE = 10;
    private User lastFollowee;
    private boolean hasMorePages;
    private View view;

    private boolean isLoading = false;

    private FollowService followService;


    //This is an observer
    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void addFollowees(List<User> followees);


    }

    public FollowingPresentor(View view) {
        this.view = view;
        followService = new FollowService();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void loadMoreItems(User user) {
        isLoading = true;
        view.setLoadingFooter(true);

        followService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollowee, new GetFollowingObserver());
    }

    private class GetFollowingObserver implements FollowService.GetFollowingObserver {

        @Override
        public void addFollowees(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            view.addFollowees(followees);
            FollowingPresentor.this.hasMorePages = hasMorePages;
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
            view.displayMessage("Failed to get folloiwing because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }
    }
}
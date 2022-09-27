package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresentor {

    private View view;

    private FollowService followService;

    private boolean hasMorePages;
    private static final int PAGE_SIZE = 10;
    private Status lastStatus;
    private boolean isLoading = false;


    public interface View {
        void displayMessage(String string);
        void setIntent(User user);
        void setLoadingFooter(boolean bool);
    }

    public void loadMoreFeedUser(String username) {
        followService.loadMoreFeed(username, new FeedPresentor.GetFeedObserver());
        view.displayMessage("Getting user's profile...Following()");
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public FeedPresentor(View view){
        this.view = view;
        followService = new FollowService();
    }

    private class GetFeedObserver implements FollowService.GetFeedObserver {


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
        public void addFeedItems(List<Status> status, boolean hasMorePages) {

        }


        @Override
        public void setIntent(User user) {
            view.setIntent(user);
        }
    }


}

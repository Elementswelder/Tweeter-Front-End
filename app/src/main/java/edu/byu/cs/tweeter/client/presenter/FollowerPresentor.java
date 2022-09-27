package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresentor {

    private View view;
    private FollowService followService;
    private boolean isLoading = false;

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
    }

    public FollowerPresentor(View view){
        this.view = view;
        followService = new FollowService();
    }

    public void loadMoreFollowers(String username) {
        followService.loadMoreFollowersFollower(username, new FollowerPresentor.GetFollowerObserver());
        view.displayMessage("Getting user's profile...Followers");
    }

}

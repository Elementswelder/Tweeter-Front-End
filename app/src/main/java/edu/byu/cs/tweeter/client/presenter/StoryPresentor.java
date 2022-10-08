package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresentor {

    private View view;
    private StatusService statusService;
    private FollowService followService;
    private static final int PAGE_SIZE = 10;

    private boolean isLoading = false;
    private Status lastStatus;
    private boolean hasMorePages;

    public interface View {
        void displayMessage(String message);
        void setLoadingFooter(boolean value);
        void setIntent(User user);
        void addStatus(List<Status> followers);
    }

    private class GetStoryObserver implements StatusService.GetStoryObserver{

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
        public void addStoryItems(List<Status> feeds, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (feeds.size() > 0) ? feeds.get(feeds.size() - 1) : null;
            view.addStatus(feeds);
            StoryPresentor.this.hasMorePages = hasMorePages;
        }
    }

    public StoryPresentor(View view){
        this.view = view;
        statusService = new StatusService();
        followService = new FollowService();
    }

    public void loadMoreFollowers(String username) {
        followService.getUser(username, new FollowService.GetUserObserver() {
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
        });
        view.displayMessage("Getting user's profile...Story");
    }

    public boolean isLoading() {
        return isLoading;
    }

    public boolean HasMorePages() {
        return hasMorePages;
    }

    public void getMoreStories(User user){
        isLoading = true;
        view.setLoadingFooter(true);
        statusService.loadMoreStatus(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new GetStoryObserver());
    }

}

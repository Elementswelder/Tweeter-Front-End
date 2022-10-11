package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
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

    public StoryPresentor(View view){
        this.view = view;
        statusService = new StatusService();
        followService = new FollowService();
    }

    public void loadMoreFollowers(String username) {
        followService.getUser(username, new GetSingleUserObserver() {
            @Override
            public void handleFailure(String message) {
                isLoading = false;
                view.displayMessage("Failed to get following: " + message);
                view.setLoadingFooter(false);
            }

            @Override
            public void handleException(Exception exception) {
                isLoading = false;
                view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
                view.setLoadingFooter(false);
            }

            @Override
            public void handleSuccess(User user) {
                view.setIntent(user);
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
                user, PAGE_SIZE, lastStatus, new PagedObserver<Status>() {
                    @Override
                    public void handleSuccess(List<Status> items, boolean hasMorePages) {
                        isLoading = false;
                        view.setLoadingFooter(false);
                        lastStatus = (items.size() > 0) ? items.get(items.size() - 1) : null;
                        view.addStatus(items);
                        StoryPresentor.this.hasMorePages = hasMorePages;
                    }

                    @Override
                    public void handleFailure(String message) {
                        isLoading = false;
                        view.displayMessage("Failed to get following: " + message);
                        view.setLoadingFooter(false);
                    }

                    @Override
                    public void handleException(Exception exception) {
                        isLoading = false;
                        view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
                        view.setLoadingFooter(false);
                    }
                });
    }
}

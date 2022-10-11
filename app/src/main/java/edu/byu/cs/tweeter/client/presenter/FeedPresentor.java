package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresentor {

    private View view;
    private FollowService followService;
    private StatusService statusService;

    private boolean hasMorePages;
    private static final int PAGE_SIZE = 10;
    private Status lastStatus;
    private boolean isLoading = false;


    public interface View {
        void displayMessage(String string);
        void setIntent(User user);
        void setLoadingFooter(boolean bool);
        void addFeeds(List<Status> statuses);
    }

    public void loadMoreFeedUser(String username) {
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
        view.displayMessage("Getting user's profile...Feed");
    }

    public void loadMoreFeed(User user) {
        isLoading = true;
        view.setLoadingFooter(true);

        followService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastStatus, new PagedObserver<>() {
                    @Override
                    public void handleSuccess(List<Status> items, boolean hasMorePages) {
                        isLoading = false;
                        view.setLoadingFooter(false);
                        lastStatus = (items.size() > 0) ? items.get(items.size() - 1) : null;
                        view.addFeeds(items);
                        FeedPresentor.this.hasMorePages = hasMorePages;
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

    public boolean isLoading() {
        return isLoading;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public FeedPresentor(View view){
        this.view = view;
        followService = new FollowService();
        statusService = new StatusService();
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
            isLoading = false;
            view.setLoadingFooter(false);
            lastStatus = (status.size() > 0) ? status.get(status.size() - 1) : null;
            view.addFeeds(status);
            FeedPresentor.this.hasMorePages = hasMorePages;

        }


        @Override
        public void setIntent(User user) {
            view.setIntent(user);
        }
    }


}

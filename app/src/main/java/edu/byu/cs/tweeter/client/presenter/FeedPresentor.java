package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresentor extends PagedPresenter<Status> {

    private PagedView<Status> view;
    private FollowService followService;

    public FeedPresentor(PagedPresenter.PagedView<Status> view){
        super(view);
        this.view = view;
        followService = new FollowService();
    }

    @Override
    protected String getDescription() {
        return null;
    }

    public void loadMoreFeedUser(String username) {
        followService.getUser(username, new MyGetSingleUserObserver());
        view.displayMessage("Getting user's profile...Feed");
    }

    public void loadMoreFeed(User user) {
        isLoading = true;
        view.setLoadingFooter(true);
        followService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItem, new StatusPagedObserver());
    }

    private class StatusPagedObserver implements PagedObserver<Status> {
        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
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
    }

    private class MyGetSingleUserObserver implements GetSingleUserObserver {
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
    }
}

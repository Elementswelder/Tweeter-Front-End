package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresentor extends PagedPresenter<User>{

    private User lastFollowee;
    private PagedView<User> view;


    private FollowService followService;

    public FollowingPresentor(PagedPresenter.PagedView<User> view) {
        super(view);
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
        followService.loadMoreItems(Cache.getInstance().getCurrUserAuthToken(), user, PAGE_SIZE, lastFollowee, new UserPagedObserver());
    }

    public void loadMoreFollowers(String username){
        followService.getUser(username, new MyGetSingleUserObserver());
        view.displayMessage("Getting user's profile...Following()");
    }

    @Override
    protected String getDescription() {
        return null;
    }

    private class UserPagedObserver implements PagedObserver<User> {
        @Override
        public void handleSuccess(List<User> followees, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollowee = (followees.size() > 0) ? followees.get(followees.size() - 1) : null;
            view.addItems(followees);
            FollowingPresentor.this.hasMorePages = hasMorePages;
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
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

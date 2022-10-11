package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User>{

    private PagedView<User> view;
    private FollowService followService;
    private User lastFollower;

    public FollowerPresenter(PagedView<User> view){
        super(view);
        this.view = view;
        followService = new FollowService();
    }

    public void loadMoreFollowers(String username) {
        followService.getUser(username, new MyGetSingleUserObserver());
        view.displayMessage("Getting user's profile...Followers");
    }

    public void getMoreFollowers(User user){
        isLoading = true;
        view.setLoadingFooter(true);
        followService.loadMoreItemsFollowers(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastFollower, new UserPagedObserver());
    }

    @Override
    protected String getDescription() {
        return null;
    }

    private class UserPagedObserver implements PagedObserver<User> {

        @Override
        public void handleSuccess(List<User> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastFollower = (items.size() > 0) ? items.get(items.size() - 1) : null;
            view.addItems(items);
            FollowerPresenter.this.hasMorePages = hasMorePages;
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

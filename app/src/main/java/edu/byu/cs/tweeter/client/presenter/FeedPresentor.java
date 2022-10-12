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

}

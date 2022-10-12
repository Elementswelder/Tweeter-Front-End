package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresentor extends PagedPresenter<Status> {

    private PagedView<Status> view;

    public FeedPresentor(PagedPresenter.PagedView<Status> view){
        super(view);
        this.view = view;
    }

    @Override
    public void setupLoading(User user) {
        followService.loadMoreItemsFeed(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItem, new NewPagedObserver());
    }
}

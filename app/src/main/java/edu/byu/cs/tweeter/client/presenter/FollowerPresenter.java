package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User>{

    private PagedView<User> view;

    public FollowerPresenter(PagedView<User> view){
        super(view);
        this.view = view;
    }

    @Override
    public void setupLoading(User user) {
        followService.loadMoreItemsFollowers(Cache.getInstance().getCurrUserAuthToken(),
                user, PAGE_SIZE, lastItem, new NewPagedObserver());
    }
}

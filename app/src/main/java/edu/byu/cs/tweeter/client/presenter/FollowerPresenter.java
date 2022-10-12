package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowerPresenter extends PagedPresenter<User>{

    private PagedView<User> view;
    private FollowService followService;

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
                user, PAGE_SIZE, lastItem, new UserPagedObserver());
    }
}

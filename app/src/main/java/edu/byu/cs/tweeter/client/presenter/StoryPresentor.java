package edu.byu.cs.tweeter.client.presenter;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresentor extends PagedPresenter<Status>{

    private PagedView<Status> view;

    public StoryPresentor(PagedView<Status> view){
        super(view);
        this.view = view;
    }

    @Override
    public void setupLoading(User user) {
        statusService.loadMoreStatus(Cache.getInstance().getCurrUserAuthToken(),
                user, 10, lastItem, new NewPagedObserver());
    }
}

package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;


public abstract class Presenter<T extends Presenter.View> {

    protected T view;
    protected static final int PAGE_SIZE = 10;
    protected FollowService followService;
    protected UserService userService;
    protected StatusService statusService;


    public Presenter(T view){
        this.view = view;
        this.userService = new UserService();
        this.followService = new FollowService();
        this.statusService = new StatusService();
    }

    public interface View {
        void displayMessage(String message);
    }

}

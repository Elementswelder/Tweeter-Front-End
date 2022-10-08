package edu.byu.cs.tweeter.client.presenter;

import android.view.MenuItem;
import android.view.View;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.FollowHandler;
import edu.byu.cs.tweeter.client.backgroundTask.Handlers.UnfollowHandler;
import edu.byu.cs.tweeter.client.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresentor {

    private View view;
    private FollowService followService;
    private StatusService statusService;
    private UserService userService;


    public interface View {

        void displayMessage(String s);
        void setFollowerButton(boolean isFollower);
        void setFollowingText(String text);
        void setFollowerText(String text);
        void logout();
        void setFollowerButtonVisable();
        void setFollowerButtonGone();
    }

    public MainPresentor(View view){
        this.view = view;
        followService = new FollowService();
        statusService = new StatusService();
        userService = new UserService();
    }

    public void setFollowingStatus(User selectedUser) {
        followService.getFollowStatus(selectedUser, new FollowService.IsFollowerObserver() {
            @Override
            public void displayException(Exception ex) {
                view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
            }

            @Override
            public void setFollowingButton(boolean isFollowed) {
                view.setFollowerButton(isFollowed);
            }

            @Override
            public void displayErrorMessage(String message) {
                view.displayMessage("Failed to get following: " + message);
            }
        });

    }
    public void setFollowButton(String buttonText, User selectedUser) {
        if (buttonText.equals("Following")) {
            followService.unfollowUser(selectedUser, new FollowService.GetUnfollowObserver() {
                @Override
                public void setFollowingButton(boolean isFollowed) {
                    view.setFollowerButton(isFollowed);
                }

                @Override
                public void displayErrorMessage(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void displayException(Exception ex) {
                    view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
                }
            },  new MainPresentor.GetMainObserver());

            view.displayMessage("Removing " + selectedUser.getName() + "...");

        } else {
            followService.followUser(selectedUser, new FollowService.GetFollowerUserObserver() {
                @Override
                public void setFollowingButton(boolean isFollowed) {
                    view.setFollowerButton(isFollowed);
                }

                @Override
                public void displayErrorMessage(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void displayException(Exception ex) {
                    view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
                }
            },  new MainPresentor.GetMainObserver());
            view.displayMessage("Adding " + selectedUser.getName() + "...");
        }
    }

    public void updateSelectedUserFollowingAndFollowers(User user) {
        followService.updateSelectedUserFollowingAndFollowers(user, new MainPresentor.GetMainObserver());
    }
    public void createNewStatus(String post) {
        statusService.createNewPost(post, new MainPresentor.GetMainObserver());
    }
    public boolean attemptLogout(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            view.displayMessage("Logging Out...");
            userService.logout(new MainPresentor.GetMainObserver());
            return true;
        } else {
            return false;
        }
    }
    public void checkUserNull(User selectedUser) {
        if (selectedUser == null){
            view.displayMessage("User NOT passed to activity");
        }
    }


    public void checkFollowButton(User selectedUser) {
        if (selectedUser.compareTo(Cache.getInstance().getCurrUser()) == 0) {
            view.setFollowerButtonGone();
        } else {
            view.setFollowerButtonVisable();
            setFollowingStatus(selectedUser);
        }
    }




    public class GetMainObserver implements FollowService.MainObserver, StatusService.GetMainObserver, UserService.GetMainObserver{

        @Override
        public void displayErrorMessage(String message) {
            view.displayMessage("Failed to get following: " + message);

        }

        @Override
        public void displayMessage(String message) {
            view.displayMessage(message);
        }

        @Override
        public void logoutUser() {
            view.logout();

        }

        @Override
        public void displayException(Exception ex) {
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());

        }

        @Override
        public void setFollowerText(String text) {
            view.setFollowerText(text);
        }

        @Override
        public void setFollowingText(String text) {
            view.setFollowingText(text);
        }

    }

}

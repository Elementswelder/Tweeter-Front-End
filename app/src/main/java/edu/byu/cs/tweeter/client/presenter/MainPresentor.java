package edu.byu.cs.tweeter.client.presenter;

import android.view.MenuItem;
import android.view.View;

import java.text.ParseException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;

import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
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
            followService.unfollowUser(selectedUser, new SimpleNotifyObserver() {
               /* @Override
                public void setFollowingButton(boolean isFollowed) {
                    view.setFollowerButton(isFollowed);
                } */

                @Override
                public void handleFailure(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void handleException(Exception exception) {
                    view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
                }

                @Override
                public void handleSuccess() {
                    view.setFollowerButton(false);
                    followService.updateSelectedUserFollowingAndFollowers(selectedUser, new FollowService.GetCountFollowObserver() {
                        @Override
                        public void setFollowerText(String text) {
                            view.setFollowingText(text);
                        }

                        @Override
                        public void setFollowingText(String text) {
                            view.setFollowerText(text);
                        }

                        @Override
                        public void displayErrorMessage(String message) {
                            view.displayMessage("Failed to get following: " + message);
                        }

                        @Override
                        public void displayException(Exception ex) {
                            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
                        }
                    });
                    setFollowingButton(false);
                }
            });

            view.displayMessage("Removing " + selectedUser.getName() + "...");

        } else {
            followService.followUser(selectedUser, new SimpleNotifyObserver() {
                @Override
                public void handleFailure(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void handleException(Exception ex) {
                    view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
                }

                @Override
                public void handleSuccess() {
                    followService.updateSelectedUserFollowingAndFollowers(selectedUser, new FollowService.GetCountFollowObserver() {
                        @Override
                        public void setFollowerText(String text) {
                            view.setFollowingText(text);
                        }

                        @Override
                        public void setFollowingText(String text) {
                            view.setFollowerText(text);
                        }

                        @Override
                        public void displayErrorMessage(String message) {
                            view.displayMessage("Failed to get following: " + message);
                        }

                        @Override
                        public void displayException(Exception ex) {
                            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
                        }
                    });
                }
            });
            setFollowingButton(true);
            view.displayMessage("Adding " + selectedUser.getName() + "...");
        }
    }

    private void setFollowingButton(boolean isFollowed) {
        view.setFollowerButton(isFollowed);
    }

    public void updateSelectedUserFollowingAndFollowers(User user) {
        followService.updateSelectedUserFollowingAndFollowers(user, new FollowService.GetCountFollowObserver() {
            @Override
            public void setFollowerText(String text) {
                view.setFollowingText(text);
            }

            @Override
            public void setFollowingText(String text) {
                view.setFollowerText(text);
            }

            @Override
            public void displayErrorMessage(String message) {
                view.displayMessage("Failed to get following: " + message);
            }

            @Override
            public void displayException(Exception ex) {
                view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
            }
        });
    }

    public void createNewStatus(String post) {
        statusService.createNewPost(post, new SimpleNotifyObserver() {
            @Override
            public void handleSuccess() {
                view.displayMessage("Successfully Posted!");
            }

            @Override
            public void handleFailure(String message) {
                view.displayMessage(message);
            }

            @Override
            public void handleException(Exception exception) {
                view.displayMessage(exception.toString());
            }
        });
    }

    public boolean attemptLogout(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            view.displayMessage("Logging Out...");
            userService.logout(new SimpleNotifyObserver() {
                @Override
                public void handleFailure(String message) {
                    view.displayMessage(message);
                }

                @Override
                public void handleException(Exception exception) {
                    view.displayMessage(exception.toString());
                }

                @Override
                public void handleSuccess() {
                    view.logout();
                }
            });
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
}

package edu.byu.cs.tweeter.client.presenter;

import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowCountObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.FollowerObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.FollowService;
import edu.byu.cs.tweeter.client.service.StatusService;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
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
        followService.getFollowStatus(selectedUser, new MyFollowerObserver());

    }
    public void setFollowButton(String buttonText, User selectedUser) {
        if (buttonText.equals("Following")) {
            followService.unfollowUser(selectedUser, new MySimpleNotifyObserver(selectedUser));
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
                    followService.updateSelectedUserFollowingAndFollowers(selectedUser, new MyFollowCountObserver());
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
        followService.updateSelectedUserFollowingAndFollowers(user, new MyFollowCountObserver());
    }

    public void createNewStatus(String post) {
        Status newStatus = new Status(post, Cache.getInstance().getCurrUser(), getFormattedDateTime(), parseURLs(post), parseMentions(post));
        statusService.createNewPost(newStatus, new SimpleNotifyObserver() {
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

    public String getFormattedDateTime() {
        try {
            SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

            return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
        }
        catch (Exception ex) {
            System.out.println(ex);
        }
        return null;
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }

    private class MyFollowerObserver implements FollowerObserver {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(boolean isFollower) {
            view.setFollowerButton(isFollower);
        }
    }

    private class MySimpleNotifyObserver implements SimpleNotifyObserver {
        private final User selectedUser;

        public MySimpleNotifyObserver(User selectedUser) {
            this.selectedUser = selectedUser;
        }

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
            followService.updateSelectedUserFollowingAndFollowers(selectedUser, new FollowCountObserver() {
                @Override
                public void handleFailure(String message) {
                    view.displayMessage("Failed to get following: " + message);
                }

                @Override
                public void handleException(Exception exception) {
                    view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
                }

                @Override
                public void handleSuccess(String followNum) {
                    view.setFollowingText(followNum);
                    view.setFollowerText(followNum);
                }
            });
            setFollowingButton(false);
        }
    }

    private class MyFollowCountObserver implements FollowCountObserver {
        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to get following: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
        }

        @Override
        public void handleSuccess(String followNum) {
            view.setFollowingText(followNum);
            view.setFollowerText(followNum);
        }
    }
}

package edu.byu.cs.tweeter.client.presenter;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.client.backgroundTask.observer.LoginsObserver;
import edu.byu.cs.tweeter.client.service.UserService;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    String imageToString;


    public interface View {
        void displayInfoMessage(String message);
        void clearInfoMessage();
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void navigateUser(User user);

    }

    private View view;
    private final UserService userService;

    public RegisterPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }

    public void initiateRegister(String firstName, String lastName, String username, String password) {
        String message = validateRegister(firstName, lastName, username, password); // This should go to the presenter
        if (message == null) {
            view.clearErrorMessage();
            view.displayInfoMessage("Registering");
            userService.register(firstName,lastName,username,password,this.imageToString, new LoginsObserver() {
                @Override
                public void handleFailure(String message) {
                    view.clearInfoMessage();
                    view.displayErrorMessage(message);
                }

                @Override
                public void handleException(Exception exception) {
                    view.clearInfoMessage();
                    view.displayErrorMessage(exception.getMessage());
                }

                @Override
                public void handleSuccess(User user, AuthToken authToken) {
                    view.displayInfoMessage("Welcome " + user.firstName);
                    view.clearErrorMessage();
                    view.navigateUser(user);
                }
            });
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage(message);
        }
    }

    public String validateRegister(String firstName, String lastName, String username, String password) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }

    public void checkImage(Bitmap bitmap) {
        if (bitmap != null) {
            Bitmap image = bitmap;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] imageBytes = bos.toByteArray();

            // Intentionally, Use the java Base64 encoder so it is compatible with M4.
            this.imageToString = Base64.getEncoder().encodeToString(imageBytes);
        }
        else {
            view.clearInfoMessage();
            view.displayErrorMessage("Error with the image");
        }
    }
}

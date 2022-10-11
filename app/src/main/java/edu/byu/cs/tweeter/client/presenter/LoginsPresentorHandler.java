package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginsPresentorHandler {

    private View view;

    public LoginsPresentorHandler(LoginsPresentorHandler.View view){
        this.view = view;
    }
}
    public interface View {
        void displayInfoMessage(String message);
        void clearInfoMessage();
        void displayErrorMessage(String message);
        void clearErrorMessage();
        void navigateUser(User user);
    }

    public void handleException(Exception exception) {
        view.clearInfoMessage();
        view.displayErrorMessage(exception.getMessage());
    }

    public void handleSuccess(User user, AuthToken authToken) {
        view.displayInfoMessage("Welcome " + user.firstName);
        view.clearErrorMessage();
        view.navigateUser(user);
}

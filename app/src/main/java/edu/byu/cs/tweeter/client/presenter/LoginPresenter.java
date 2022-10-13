package edu.byu.cs.tweeter.client.presenter;

public class LoginPresenter extends LoginsPresenter {


    public LoginPresenter(LoginsPresenter.LoginsView view) {
        super(view);
    }

    public void initiateLogin(String username, String password) {
        String message = validateLogin(username, password); // This should go to the presenter
        if (message == null) {
            view.clearMessage();
            view.displayMessage("Logging In");
            userService.login(username, password, new LoginRegisterObserver());
        }
        else {
            view.clearMessage();
            view.displayErrorMessage(message);
        }
    }

    public String validateLogin(String username, String password) {
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
}

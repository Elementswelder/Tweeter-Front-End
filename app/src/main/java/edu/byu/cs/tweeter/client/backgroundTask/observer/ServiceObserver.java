package edu.byu.cs.tweeter.client.backgroundTask.observer;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception exception);
    void handleSuccess();
}


// I'M GOING TO NEED TO CREATE A HEIRACHY OF THESE SERVICECLASSES FOR THE SUCESS MESSAGES
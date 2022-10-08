package edu.byu.cs.tweeter.client.backgroundTask.observer;

public interface ServiceObserver {
    void handleFailure(String message);
    void handleException(Exception exception);
}


// I'M GOING TO NEED TO CREATE A HEIRACHY OF THESE SERVICECLASSES FOR THE SUCESS MESSAGES
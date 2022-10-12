package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.backgroundTask.observer.GetSingleUserObserver;
import edu.byu.cs.tweeter.client.backgroundTask.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter{
    protected PagedView<T> view;
    protected T lastItem;
    protected static final int PAGE_SIZE = 10;
    protected boolean isLoading = false;
    protected boolean hasMorePages;
    AuthToken authToken;
    User targetUser;
    protected boolean isGettingUser;

    public boolean hasMorePages(){
        return hasMorePages;
    }

    public boolean isLoading(){
        return isLoading;
    }

    public void setHasMorePages(boolean hasMorePages){
        this.hasMorePages = hasMorePages;
    }

    public PagedPresenter(PagedView view){
        super(view);
        this.view = view;
    }

    public interface PagedView<T> extends Presenter.View{
        void setLoadingFooter(boolean value);
        void setIntent(User user);
        void addItems(List<T> items);
    }

    protected class MyGetSingleUserObserver implements GetSingleUserObserver {
        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
            view.setLoadingFooter(false);
        }

        @Override
        public void handleSuccess(User user) {
            view.setIntent(user);
        }
    }

    protected class UserPagedObserver implements PagedObserver<User> {

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception ex) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + ex.getMessage());
            view.setLoadingFooter(false);
        }

        @Override
        public void handleSuccess(List<User> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? (T) items.get(items.size() - 1) : null;
            view.addItems((List<T>) items);
            setHasMorePages(hasMorePages);
        }
    }

    protected class StatusPagedObserver implements PagedObserver<Status> {
        @Override
        public void handleSuccess(List<Status> items, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooter(false);
            lastItem = (items.size() > 0) ? (T) items.get(items.size() - 1) : null;
            view.addItems((List<T>) items);
            setHasMorePages(hasMorePages);
        }

        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.displayMessage("Failed to get following: " + message);
            view.setLoadingFooter(false);
        }

        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.displayMessage("Failed to get following because of exception: " + exception.getMessage());
            view.setLoadingFooter(false);
        }
    }
}

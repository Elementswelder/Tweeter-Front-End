package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter{

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
    }

    public interface PagedView<T> extends Presenter.View{
        void setLoadingFooter(boolean value);
        void setIntent(User user);
        void addItems(List<T> items);
    }
    protected abstract String getDescription();

}

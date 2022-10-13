package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.backgroundTask.observer.SimpleNotifyObserver;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.service.StatusService;

public class MainPresentroUnitTest {

    private MainPresenter.MainView mockView;
    private Cache mockCache;
    private StatusService mockStatusService;

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockCache = Mockito.mock(Cache.class);
        mockStatusService = Mockito.mock(StatusService.class);

        Cache.setInstance(mockCache);

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));
        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);
    }

    @Test
    public void postStatusSuccess() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotifyObserver observer = invocation.getArgument(0, SimpleNotifyObserver.class);
                observer.handleSuccess();
                return null;
            }
        };

        mainPresenterSpy.createNewStatus(Mockito.anyString());
        Mockito.doAnswer(answer).when(mockStatusService).createNewPost(Mockito.anyString(), Mockito.any());
       // Mockito.doAnswer(answer).when(mainPresentorSpy).createNewStatus(Mockito.anyString());
        Mockito.verify(mockView).displayMessage("Successfully Posted!");

    }

    @Test
    public void postStatusFail() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotifyObserver observer = invocation.getArgument(0, SimpleNotifyObserver.class);
                observer.handleFailure("Something bad happened");
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).createNewPost(Mockito.anyString(), Mockito.any());
        mainPresenterSpy.createNewStatus(Mockito.anyString());
        Mockito.verify(mockView).displayMessage("Something bad happened");

    }

    @Test
    public void postStatusException(){
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                SimpleNotifyObserver observer = invocation.getArgument(0, SimpleNotifyObserver.class);
                observer.handleException(new Exception("New Exception"));
                return null;
            }
        };

        Mockito.doAnswer(answer).when(mockStatusService).createNewPost(Mockito.anyString(), Mockito.any());
        mainPresenterSpy.createNewStatus(Mockito.anyString());
        Mockito.verify(mockView).displayMessage("New Exception");
    }
}

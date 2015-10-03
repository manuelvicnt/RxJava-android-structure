package manuelvicnt.com.rxjava_android_structure.networking.registration;

import android.content.Context;
import android.test.InstrumentationTestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import manuelvicnt.com.rxjava_android_structure.data.PrivateSharedPreferencesManager;
import manuelvicnt.com.rxjava_android_structure.networking.registration.exception.RegistrationNicknameAlreadyExistsException;
import manuelvicnt.com.rxjava_android_structure.networking.registration.exception.RegistrationTechFailureException;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class RegistrationAPIServiceTest extends InstrumentationTestCase {

    @Mock IRegistrationAPI mockRegistrationAPI;
    @Mock RestAdapter mockRestAdapter;
    @Mock PrivateSharedPreferencesManager mockPrivateSharedPreferencesManager;

    private RegistrationResponse registrationResponse;
    private RegistrationRequest registrationRequest;

    private RegistrationAPIService registrationAPIService;

    @Before
    protected void setUp() throws Exception {
        super.setUp();

        MockitoAnnotations.initMocks(this);

        registrationResponse = new RegistrationResponse();
        registrationRequest = new RegistrationRequest("", "");
        registrationAPIService = new RegistrationAPIService(mockRestAdapter, mockPrivateSharedPreferencesManager);

        doReturn(mockRegistrationAPI).when(mockRestAdapter).create(any(Class.class));
        registrationAPIService.setRegistrationAPI(mockRegistrationAPI);
    }

    @After
    public void tearDown() {

        registrationResponse = null;
        registrationRequest = null;
        registrationAPIService = null;
    }

    @Test
    public void testRegister_ReturnsRegistrationResponse() {

        doReturn(Observable.just(registrationResponse)).
                when(mockRegistrationAPI).register(any(RegistrationRequest.class));

        TestSubscriber testSubscriber = new TestSubscriber();

        registrationAPIService.register(registrationRequest)
                .subscribe(testSubscriber);

        RegistrationResponse result = (RegistrationResponse) testSubscriber.getOnNextEvents().get(0);
        assertEquals(registrationResponse, result);
    }

    @Test
    public void testRegister_SetsRequestingToTrueWhenSubscribed() {

        // Avoid threading problems, it finishes as soon as someone is subscribed.
        doReturn(Observable.never()).when(mockRegistrationAPI).register(anyObject());

        registrationAPIService.register(registrationRequest).subscribe();

        assertTrue(registrationAPIService.isRequestingRegistration());
    }

    @Test
    public void testRegister_SetsRequestingToFalseWhenOnError() {

        // We have to wrap it around a try-catch since is going to throw the exception
        // when onError is called
        doReturn(Observable.error(null)).when(mockRegistrationAPI).register(anyObject());

        try {
            registrationAPIService.register(registrationRequest).subscribe();
        } catch (Exception e) {
            // Do Nothing
        }

        assertFalse(registrationAPIService.isRequestingRegistration());
    }

    @Test
    public void testRegister_SetsRequestingToFalseWhenOnComplete() {

        // Avoid threading problems, it finishes as soon as someone is subscribed.
        doReturn(Observable.empty()).when(mockRegistrationAPI).register(anyObject());

        registrationAPIService.register(registrationRequest).subscribe();

        assertFalse(registrationAPIService.isRequestingRegistration());
    }

    @Test
    public void testRegister_throwsRegistrationNicknameAlreadyExistsExceptionWhen401Error() {

        RetrofitError retrofitError = RetrofitError.httpError("", new Response("", 401, "", new ArrayList<>(), null), null, null);

        doReturn(Observable.error(retrofitError)).when(mockRegistrationAPI).register(anyObject());

        TestSubscriber testSubscriber = new TestSubscriber();
        registrationAPIService.register(registrationRequest).subscribe(testSubscriber);

        Throwable resultError = (Throwable) testSubscriber.getOnErrorEvents().get(0);
        assertTrue(resultError instanceof RegistrationNicknameAlreadyExistsException);
    }

    @Test
    public void testRegister_throwsRegistrationTechFailureExceptionWhenOtherError() {

        RetrofitError retrofitError = RetrofitError.httpError("", new Response("", 400, "", new ArrayList<>(), null), null, null);

        doReturn(Observable.error(retrofitError)).when(mockRegistrationAPI).register(anyObject());

        TestSubscriber testSubscriber = new TestSubscriber();
        registrationAPIService.register(registrationRequest).subscribe(testSubscriber);

        Throwable resultError = (Throwable) testSubscriber.getOnErrorEvents().get(0);
        assertTrue(resultError instanceof RegistrationTechFailureException);
    }

    @Test
    public void testRegister_storesUserNicknameOnSharedPreferencesWhenResponseSuccessful() {

        String expected = "nickname";
        RegistrationRequest registrationRequest = new RegistrationRequest(expected, "");

        doReturn(Observable.just(registrationResponse)).when(mockRegistrationAPI).register(registrationRequest);

        // Threading problems here. We have to make sure that it's called onNext before checking it.
        // One way of doing it is forcing the Observable to finish, using a testSubscriber and calling onCompleted
        TestSubscriber testSubscriber = new TestSubscriber();
        registrationAPIService.register(registrationRequest).subscribe(testSubscriber);
        testSubscriber.onCompleted();

        verify(mockPrivateSharedPreferencesManager).storeUserNickname(expected);
    }

}
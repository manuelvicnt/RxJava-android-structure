package manuelvicnt.com.rxjava_android_structure.networking;

import android.content.Context;

import manuelvicnt.com.rxjava_android_structure.data.AuthenticationManager;
import manuelvicnt.com.rxjava_android_structure.data.PrivateSharedPreferencesManager;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginAPIService;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginRequest;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginResponse;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginInternalException;
import manuelvicnt.com.rxjava_android_structure.networking.mock.RestAdapterFactory;
import manuelvicnt.com.rxjava_android_structure.networking.registration.RegistrationAPIService;
import manuelvicnt.com.rxjava_android_structure.networking.registration.RegistrationRequest;
import manuelvicnt.com.rxjava_android_structure.networking.registration.RegistrationResponse;
import manuelvicnt.com.rxjava_android_structure.networking.registration.exception.RegistrationInternalException;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class AuthenticationRequestManager {

    private static AuthenticationRequestManager instance;

    private AuthenticationManager authenticationManager;
    private PrivateSharedPreferencesManager privateSharedPreferencesManager;

    private RegistrationAPIService registrationAPIService;
    private LoginAPIService loginAPIService;
    private UserDataRequestManager userDataRequestManager;

    private AuthenticationRequestManager(Context context) {

        this.authenticationManager = AuthenticationManager.getInstance();

        privateSharedPreferencesManager = PrivateSharedPreferencesManager.getInstance(context);
        RestAdapter restAdapter = RestAdapterFactory.getAdapter(context);

        this.registrationAPIService = new RegistrationAPIService(restAdapter, privateSharedPreferencesManager);
        this.loginAPIService = new LoginAPIService(restAdapter, authenticationManager);

        this.userDataRequestManager = UserDataRequestManager.getInstance(context);
    }

    public static AuthenticationRequestManager getInstance(Context context) {

        synchronized (AuthenticationRequestManager.class) {
            if (instance == null) {
                instance = new AuthenticationRequestManager(context);
            }

            return instance;
        }
    }

    public Observable<Object> register() {

        return registrationAPIService.register(createBodyForRegistration())
                .flatMap(this::makeLoginRequest);
    }

    public Observable<Object> login() {

        return loginAPIService.login(createLoginRequest())
                .flatMap(this::makeGetUserDataRequest);
    }

    private Observable<Object> makeLoginRequest(RegistrationResponse registrationResponse) {

        return login();
    }

    private Observable<Object> makeGetUserDataRequest(LoginResponse loginResponse) {

        return userDataRequestManager.getUserData();
    }

    private LoginRequest createLoginRequest() {

        String nickname = privateSharedPreferencesManager.getUserNickname();
        String password = authenticationManager.getPassword();

        if (nickname == null || nickname.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new LoginInternalException();
        }

        return new LoginRequest(nickname, password);
    }

    private RegistrationRequest createBodyForRegistration() {

        String nickname = authenticationManager.getNickname();
        String password = authenticationManager.getPassword();

        if (nickname == null || nickname.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new RegistrationInternalException();
        }

        return new RegistrationRequest(nickname, password);
    }
}

package manuelvicnt.com.rxjava_android_structure.login;

import manuelvicnt.com.rxjava_android_structure.networking.AuthenticationRequestManager;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginResponse;
import rx.subjects.AsyncSubject;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class LoginViewModel {

    private AuthenticationRequestManager authenticationRequestManager;
    private AsyncSubject<Object> loginSubject;

    public LoginViewModel(AuthenticationRequestManager authenticationRequestManager) {

        this.authenticationRequestManager = authenticationRequestManager;
        loginSubject = AsyncSubject.create();
    }

    public AsyncSubject<Object> createLoginSubject() {

        loginSubject = AsyncSubject.create();
        return loginSubject;
    }

    public AsyncSubject<Object> getLoginSubject() {

        return loginSubject;
    }

    public void login() {

        authenticationRequestManager.login()
                .subscribe(loginSubject);
    }

}

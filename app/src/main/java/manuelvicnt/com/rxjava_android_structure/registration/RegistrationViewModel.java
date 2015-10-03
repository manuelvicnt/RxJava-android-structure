package manuelvicnt.com.rxjava_android_structure.registration;

import manuelvicnt.com.rxjava_android_structure.networking.AuthenticationRequestManager;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginResponse;
import rx.subjects.AsyncSubject;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class RegistrationViewModel {

    private AuthenticationRequestManager authenticationRequestManager;
    private AsyncSubject<Object> registrationSubject;

    public RegistrationViewModel(AuthenticationRequestManager authenticationRequestManager) {

        this.authenticationRequestManager = authenticationRequestManager;
        registrationSubject = AsyncSubject.create();
    }

    public AsyncSubject<Object> createRegistrationSubject() {

        registrationSubject = AsyncSubject.create();
        return registrationSubject;
    }

    public AsyncSubject<Object> getRegistrationSubject() {

        return registrationSubject;
    }

    public void register() {

        authenticationRequestManager.register()
                .subscribe(registrationSubject);
    }
}

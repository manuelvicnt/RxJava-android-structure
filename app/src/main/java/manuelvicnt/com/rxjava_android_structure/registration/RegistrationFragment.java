package manuelvicnt.com.rxjava_android_structure.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;
import manuelvicnt.com.rxjava_android_structure.HomeActivity;
import manuelvicnt.com.rxjava_android_structure.R;
import manuelvicnt.com.rxjava_android_structure.base.BaseFragment;
import manuelvicnt.com.rxjava_android_structure.data.AuthenticationManager;
import manuelvicnt.com.rxjava_android_structure.login.LoginFragment;
import manuelvicnt.com.rxjava_android_structure.networking.AuthenticationRequestManager;
import manuelvicnt.com.rxjava_android_structure.networking.account.exception.AccountTechFailureException;
import manuelvicnt.com.rxjava_android_structure.networking.games.exception.GamesTechFailureException;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginResponse;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginInternalException;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginTechFailureException;
import manuelvicnt.com.rxjava_android_structure.networking.registration.exception.RegistrationNicknameAlreadyExistsException;
import manuelvicnt.com.rxjava_android_structure.networking.registration.exception.RegistrationInternalException;
import manuelvicnt.com.rxjava_android_structure.networking.registration.exception.RegistrationTechFailureException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class RegistrationFragment extends BaseFragment {

    private RegistrationViewModel registrationViewModel;
    private Subscription registrationSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthenticationRequestManager authenticationRequestManager =
                AuthenticationRequestManager.getInstance(getActivity().getApplicationContext());
        registrationViewModel = new RegistrationViewModel(authenticationRequestManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_registration, container, false);

        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    protected void subscribeForNetworkRequests() {

        registrationSubscription = registrationViewModel.getRegistrationSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RegistrationSubscriber());
    }

    @Override
    protected void reconnectWithNetworkRequests() {

        registrationSubscription = registrationViewModel.createRegistrationSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new RegistrationSubscriber());
    }

    @Override
    protected void unsubscribeFromNetworkRequests() {

        if (registrationSubscription != null) {
            registrationSubscription.unsubscribe();
        }
    }

    @OnClick(R.id.register)
    public void registerButtonTap(View view) {

        AuthenticationManager.getInstance().setNickname("nickname");
        AuthenticationManager.getInstance().setPassword("password");
        registrationViewModel.register();

        progressDialog = ProgressDialog.show(getActivity(), "Registering", "...", true);
    }

    private void showMessage(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void showMessageAndGoToLogin(String message) {

        showMessage(message);

        getActivity().setTitle("Login");
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
    }

    private void launchHomeActivity() {

        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private class RegistrationSubscriber extends Subscriber<Object> {

        @Override
        public void onCompleted() {

            hideProgressDialog();
            launchHomeActivity();
        }

        @Override
        public void onError(Throwable e) {

            hideProgressDialog();

            // To be able to make another Registration request,
            // we have to reset the Subject in the VM
            reconnectWithNetworkRequests();

            if (e instanceof RegistrationInternalException
                    || e instanceof RegistrationTechFailureException) {

                showMessage("Registration Failure");

            } else if (e instanceof RegistrationNicknameAlreadyExistsException) {

                showMessage("Registration Nickname already exists");

            } else if (e instanceof LoginInternalException
                    || e instanceof LoginTechFailureException) {

                showMessageAndGoToLogin("Login Failure");

            } else if (e instanceof AccountTechFailureException) {

                showMessageAndGoToLogin("Account failed");
            } else if (e instanceof GamesTechFailureException) {

                showMessageAndGoToLogin("Games failed");
            }
        }

        @Override
        public void onNext(Object getUserDataResponse) {

        }
    }
}

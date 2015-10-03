package manuelvicnt.com.rxjava_android_structure.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import manuelvicnt.com.rxjava_android_structure.networking.AuthenticationRequestManager;
import manuelvicnt.com.rxjava_android_structure.networking.account.exception.AccountTechFailureException;
import manuelvicnt.com.rxjava_android_structure.networking.games.exception.GamesTechFailureException;
import manuelvicnt.com.rxjava_android_structure.networking.login.LoginResponse;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginInternalException;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginTechFailureException;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class LoginFragment extends BaseFragment {

    private LoginViewModel loginViewModel;
    private Subscription loginSubscription;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthenticationRequestManager authenticationRequestManager =
                AuthenticationRequestManager.getInstance(getActivity().getApplicationContext());
        loginViewModel = new LoginViewModel(authenticationRequestManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    protected void subscribeForNetworkRequests() {

        loginSubscription = loginViewModel.getLoginSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoginSubscriber());
    }

    @Override
    protected void reconnectWithNetworkRequests() {

        loginSubscription = loginViewModel.createLoginSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new LoginSubscriber());
    }

    @Override
    protected void unsubscribeFromNetworkRequests() {

        if (loginSubscription != null) {
            loginSubscription.unsubscribe();
        }
    }

    @OnClick(R.id.login)
    public void loginButtonTap(View view) {

        AuthenticationManager.getInstance().setPassword("password");
        loginViewModel.login();

        progressDialog = ProgressDialog.show(getActivity(), "Login", "...", true);
    }

    private void showMessage(String message) {

        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private void launchHomeActivity() {

        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
        getActivity().finish();
    }

    private class LoginSubscriber extends Subscriber<Object> {

        @Override
        public void onCompleted() {

            hideProgressDialog();
            launchHomeActivity();
        }

        @Override
        public void onError(Throwable e) {

            hideProgressDialog();

            // To be able to make another Login request,
            // we have to reset the Subject in the VM
            reconnectWithNetworkRequests();

            if (e instanceof LoginInternalException
                    || e instanceof LoginTechFailureException) {

                showMessage("Login Failure");

            } else if (e instanceof AccountTechFailureException) {

                showMessage("Account failed");
                launchHomeActivity();
            } else if (e instanceof GamesTechFailureException) {

                showMessage("Games failed");
                launchHomeActivity();
            }
        }

        @Override
        public void onNext(Object userDataResponse) {

        }
    }

}

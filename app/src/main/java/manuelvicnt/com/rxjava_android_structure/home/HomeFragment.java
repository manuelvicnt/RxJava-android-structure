package manuelvicnt.com.rxjava_android_structure.home;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import manuelvicnt.com.rxjava_android_structure.R;
import manuelvicnt.com.rxjava_android_structure.base.BaseFragment;
import manuelvicnt.com.rxjava_android_structure.networking.UserDataRequestManager;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class HomeFragment extends BaseFragment {

    private HomeViewModel homeViewModel;
    private Subscription userDataSubscription;

    @Bind(R.id.user_data) TextView userDataText;
    @Bind(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserDataRequestManager userDataRequestManager =
                UserDataRequestManager.getInstance(getActivity().getApplicationContext());
        homeViewModel = new HomeViewModel(userDataRequestManager);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, rootView);

        showRandomSuccessfulMessage();

        setupRefreshLayout();
        return rootView;
    }

    @Override
    protected void subscribeForNetworkRequests() {

        userDataSubscription = homeViewModel.getUserDataSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UserDataSubscriber());
    }

    @Override
    protected void reconnectWithNetworkRequests() {

        userDataSubscription = homeViewModel.createUserDataSubject()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new UserDataSubscriber());
    }

    @Override
    protected void unsubscribeFromNetworkRequests() {

        if (userDataSubscription != null) {
            userDataSubscription.unsubscribe();
        }
    }

    private void setupRefreshLayout() {

        swipeRefreshLayout.setOnRefreshListener(() -> homeViewModel.getUserData());
    }

    private void showRandomSuccessfulMessage() {

        showMessage(homeViewModel.generateRandomMessage());
    }

    private void showMessage(String message) {

        // Because we subscribe on the Main Thread, we can do this
        userDataText.setText(message);
    }

    private void hideRefreshLayout() {

        swipeRefreshLayout.setRefreshing(false);
    }

    private class UserDataSubscriber extends Subscriber<Object> {

        @Override
        public void onCompleted() {

            hideRefreshLayout();

            // To be able to pull to refresh (and make another request),
            // we have to reset the Subject in the VM
            reconnectWithNetworkRequests();
        }

        @Override
        public void onError(Throwable e) {

            hideRefreshLayout();

            // To be able to make another UserData request when it fails,
            // we have to reset the Subject in the VM
            reconnectWithNetworkRequests();

            showMessage("Error");
        }

        @Override
        public void onNext(Object userDataResponse) {

            showRandomSuccessfulMessage();
        }
    }
}
package manuelvicnt.com.rxjava_android_structure.base;

import android.app.ProgressDialog;
import android.support.v4.app.Fragment;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public abstract class BaseFragment extends Fragment {

    protected ProgressDialog progressDialog;

    @Override
    public void onResume() {

        super.onResume();
        subscribeForNetworkRequests();
    }

    @Override
    public void onPause() {

        super.onPause();
        unsubscribeFromNetworkRequests();
    }

    protected void hideProgressDialog() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    protected abstract void subscribeForNetworkRequests();
    protected abstract void unsubscribeFromNetworkRequests();
    protected abstract void reconnectWithNetworkRequests();

}

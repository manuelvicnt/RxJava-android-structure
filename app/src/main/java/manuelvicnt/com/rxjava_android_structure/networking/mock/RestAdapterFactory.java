package manuelvicnt.com.rxjava_android_structure.networking.mock;

import android.content.Context;

import manuelvicnt.com.rxjava_android_structure.networking.mock.MockHttpClient;
import retrofit.RestAdapter;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class RestAdapterFactory {

    public static RestAdapter getAdapter(Context context) {

        return createAdapter(context);
    }

    private static RestAdapter createAdapter(Context context) {

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new MockHttpClient(context))
                .setEndpoint("mock")
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter;
    }
}

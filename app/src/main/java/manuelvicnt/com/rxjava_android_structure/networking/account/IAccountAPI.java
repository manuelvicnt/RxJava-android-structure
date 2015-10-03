package manuelvicnt.com.rxjava_android_structure.networking.account;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public interface IAccountAPI {

    @GET("/account")
    Observable<AccountResponse> getAccountInformation(@Header("nickname") String nickname);
}

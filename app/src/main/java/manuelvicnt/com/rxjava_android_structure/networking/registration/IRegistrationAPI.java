package manuelvicnt.com.rxjava_android_structure.networking.registration;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public interface IRegistrationAPI {

    @POST("/registration")
    Observable<RegistrationResponse> register(@Body RegistrationRequest request);
}

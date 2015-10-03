package manuelvicnt.com.rxjava_android_structure.networking.login;

import manuelvicnt.com.rxjava_android_structure.networking.registration.RegistrationRequest;
import manuelvicnt.com.rxjava_android_structure.networking.registration.RegistrationResponse;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public interface ILoginAPI {

    @GET("/login")
    Observable<Response> login(@Header("nickname") String nickname, @Header("password") String password);
}

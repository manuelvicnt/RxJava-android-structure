package manuelvicnt.com.rxjava_android_structure.networking.login;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import manuelvicnt.com.rxjava_android_structure.data.AuthenticationManager;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginTechFailureException;
import retrofit.RestAdapter;
import retrofit.client.Response;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class LoginAPIService {

    private ILoginAPI loginAPI;
    private boolean isRequestingLogin;
    private AuthenticationManager authenticationManager;

    public LoginAPIService(RestAdapter restAdapter, AuthenticationManager authenticationManager) {

        this.loginAPI = restAdapter.create(ILoginAPI.class);
        this.authenticationManager = authenticationManager;
    }

    public boolean isRequestingLogin() {
        return isRequestingLogin;
    }

    public Observable<LoginResponse> login(LoginRequest request) {

        return loginAPI.login(request.getNickname(), request.getPassword())
                .doOnSubscribe(() -> isRequestingLogin = true)
                .doOnTerminate(() -> isRequestingLogin = false)
                .doOnError(this::handleLoginError)
                .flatMap(this::parseLoginResponse)
                .doOnNext(this::processLoginResponse);
    }

    private void handleLoginError(Throwable throwable) {

        throw new LoginTechFailureException();
    }

    private void processLoginResponse(LoginResponse loginResponse) {

        authenticationManager.logUserIn();
    }

    private Observable<LoginResponse> parseLoginResponse(Response response) {

        try {
            String body = fromStream(response.getBody().in());
            Gson gson = new Gson();

            LoginResponse loginResponse = gson.fromJson(body, LoginResponse.class);
            loginResponse.setLoginStatusResponse(response.getStatus());
            return Observable.just(loginResponse);

        } catch (IOException e) {
            return null;
        }
    }

    private String fromStream(InputStream inputStream) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String newLine = System.getProperty("line.separator");

        String line;
        while((line = reader.readLine()) != null) {
            out.append(line);
            out.append(newLine);
        }

        return out.toString();
    }
}

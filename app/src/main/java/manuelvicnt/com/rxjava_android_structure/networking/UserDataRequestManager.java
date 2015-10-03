package manuelvicnt.com.rxjava_android_structure.networking;

import android.content.Context;

import java.util.Collections;

import manuelvicnt.com.rxjava_android_structure.data.DataManager;
import manuelvicnt.com.rxjava_android_structure.model.UserData;
import manuelvicnt.com.rxjava_android_structure.networking.account.AccountAPIService;
import manuelvicnt.com.rxjava_android_structure.networking.account.AccountRequest;
import manuelvicnt.com.rxjava_android_structure.networking.account.AccountResponse;
import manuelvicnt.com.rxjava_android_structure.networking.games.GamesAPIService;
import manuelvicnt.com.rxjava_android_structure.networking.games.GamesRequest;
import manuelvicnt.com.rxjava_android_structure.networking.games.GamesResponse;
import manuelvicnt.com.rxjava_android_structure.networking.mock.RestAdapterFactory;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class UserDataRequestManager {

    private static UserDataRequestManager instance;

    private DataManager dataManager;

    private AccountAPIService accountAPIService;
    private GamesAPIService gamesAPIService;

    private UserDataRequestManager(Context context) {

        dataManager = DataManager.getInstance();

        RestAdapter restAdapter = RestAdapterFactory.getAdapter(context);
        accountAPIService = new AccountAPIService(restAdapter);
        gamesAPIService = new GamesAPIService(restAdapter);
    }

    public static UserDataRequestManager getInstance(Context context) {

        synchronized (UserDataRequestManager.class) {
            if (instance == null) {
                instance = new UserDataRequestManager(context);
            }

            return instance;
        }
    }

    public Observable<Object> getUserData() {

        return Observable.zip(
                    getAccount(),
                    getGames(),
                    this::processUserDataResult);
    }

    private Object processUserDataResult(AccountResponse accountResponse, GamesResponse gamesResponse) {

        UserData userData = dataManager.getUserData();
        // TODO: do this for real
        userData.setAccountInformation("get information from the account response");
        userData.setGames(Collections.EMPTY_LIST);

        return Observable.just(new Object());
    }

    private Observable<AccountResponse> getAccount() {

        return accountAPIService.getAccount(createAccountRequest());
    }
    
    private Observable<GamesResponse> getGames() {

        return gamesAPIService.getGames(createGamesRequest());
    }

    private GamesRequest createGamesRequest() {

        return new GamesRequest("nickname");
    }

    private AccountRequest createAccountRequest() {

        return new AccountRequest("nickname");
    }
}

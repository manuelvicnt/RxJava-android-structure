package manuelvicnt.com.rxjava_android_structure.networking.games;

import manuelvicnt.com.rxjava_android_structure.networking.account.AccountRequest;
import manuelvicnt.com.rxjava_android_structure.networking.account.AccountResponse;
import manuelvicnt.com.rxjava_android_structure.networking.account.IAccountAPI;
import manuelvicnt.com.rxjava_android_structure.networking.games.exception.GamesTechFailureException;
import manuelvicnt.com.rxjava_android_structure.networking.login.exception.LoginTechFailureException;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class GamesAPIService {

    private IGamesAPI gamesAPI;
    private boolean isRequestingGames;

    public GamesAPIService(RestAdapter restAdapter) {

        this.gamesAPI = restAdapter.create(IGamesAPI.class);
    }

    public boolean isRequestingGames() {
        return isRequestingGames;
    }

    public Observable<GamesResponse> getGames(GamesRequest request) {

        return gamesAPI.getGamesInformation(request.getNickname())
                .doOnSubscribe(() -> isRequestingGames = true)
                .doOnTerminate(() -> isRequestingGames = false)
                .doOnError(this::handleAccountError);
    }

    private void handleAccountError(Throwable throwable) {

        throw new GamesTechFailureException();
    }
}

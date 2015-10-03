package manuelvicnt.com.rxjava_android_structure.networking.games;

import retrofit.http.GET;
import retrofit.http.Header;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public interface IGamesAPI {

    @GET("/games")
    Observable<GamesResponse> getGamesInformation(@Header("nickname") String nickname);
}

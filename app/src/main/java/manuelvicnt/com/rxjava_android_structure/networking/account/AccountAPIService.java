package manuelvicnt.com.rxjava_android_structure.networking.account;

import manuelvicnt.com.rxjava_android_structure.networking.account.exception.AccountTechFailureException;
import retrofit.RestAdapter;
import rx.Observable;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class AccountAPIService {

    private IAccountAPI accountAPI;
    private boolean isRequestingAccount;

    public AccountAPIService(RestAdapter restAdapter) {

        this.accountAPI = restAdapter.create(IAccountAPI.class);
    }

    public boolean isRequestingAccount() {
        return isRequestingAccount;
    }

    public Observable<AccountResponse> getAccount(AccountRequest request) {

        return accountAPI.getAccountInformation(request.getNickname())
                .doOnSubscribe(() -> isRequestingAccount = true)
                .doOnTerminate(() -> isRequestingAccount = false)
                .doOnError(this::handleAccountError);
    }

    private void handleAccountError(Throwable throwable) {

        throw new AccountTechFailureException();
    }
}

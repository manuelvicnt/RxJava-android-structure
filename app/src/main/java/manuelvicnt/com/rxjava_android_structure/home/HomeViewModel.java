package manuelvicnt.com.rxjava_android_structure.home;

import java.util.Random;

import manuelvicnt.com.rxjava_android_structure.networking.UserDataRequestManager;
import rx.subjects.AsyncSubject;

/**
 * Created by ManuelVivo on 03/10/15.
 */
public class HomeViewModel {

    private UserDataRequestManager userDataRequestManager;
    private AsyncSubject<Object> userDataSubject;
    private Random random;

    public HomeViewModel(UserDataRequestManager userDataRequestManager) {

        this.userDataRequestManager = userDataRequestManager;
        userDataSubject = AsyncSubject.create();
        random = new Random();
    }

    public AsyncSubject<Object> createUserDataSubject() {

        userDataSubject = AsyncSubject.create();
        return userDataSubject;
    }

    public AsyncSubject<Object> getUserDataSubject() {

        return userDataSubject;
    }

    public void getUserData() {

        userDataRequestManager.getUserData()
                .subscribe(userDataSubject);
    }

    public String generateRandomMessage() {

        double nextRandom = random.nextDouble();
        return Double.toString(nextRandom);
    }
}

package manuelvicnt.com.rxjava_android_structure;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import manuelvicnt.com.rxjava_android_structure.data.PrivateSharedPreferencesManager;
import manuelvicnt.com.rxjava_android_structure.login.LoginFragment;
import manuelvicnt.com.rxjava_android_structure.registration.RegistrationFragment;

public class LandingActivity extends AppCompatActivity {

    @Bind(R.id.toolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (savedInstanceState != null) {
            return;
        }


        String userNickname = PrivateSharedPreferencesManager.getInstance(getApplicationContext()).getUserNickname();
        Fragment initialFragment;

        if (userNickname == null || userNickname.isEmpty()) {

            setTitle("Registration");
            initialFragment = new RegistrationFragment();
        } else {

            setTitle("Login");
            initialFragment = new LoginFragment();
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, initialFragment).commit();

    }
}


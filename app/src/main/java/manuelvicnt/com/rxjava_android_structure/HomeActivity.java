package manuelvicnt.com.rxjava_android_structure;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import manuelvicnt.com.rxjava_android_structure.base.BaseFragment;
import manuelvicnt.com.rxjava_android_structure.home.HomeFragment;
import manuelvicnt.com.rxjava_android_structure.registration.RegistrationFragment;

public class HomeActivity extends AppCompatActivity {

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

        setTitle("Home");
        HomeFragment homeFragment = new HomeFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, homeFragment).commit();
    }

}


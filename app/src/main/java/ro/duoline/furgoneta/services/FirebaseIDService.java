package ro.duoline.furgoneta.services;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Paul on 12/04/2018.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        //SaveSharedPreferences.setDevicetoken(getApplicationContext(), token);
    }
}

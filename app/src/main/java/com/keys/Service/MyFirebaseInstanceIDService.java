package com.keys.Service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshToken);
    }

    private void sendRegistrationToServer(String token) {

    }
}

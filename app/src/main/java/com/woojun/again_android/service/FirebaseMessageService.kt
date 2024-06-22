package com.woojun.again_android.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.woojun.again_android.database.Preferences.saveFCM

class FirebaseMessageService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        saveFCM(this, token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
    }

}
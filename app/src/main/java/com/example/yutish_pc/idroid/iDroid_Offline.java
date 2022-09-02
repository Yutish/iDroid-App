package com.example.yutish_pc.idroid;
//
import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Yutish-pc on 17-02-2018.
 */

public class iDroid_Offline extends Application {                                                    //for offline usage

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}

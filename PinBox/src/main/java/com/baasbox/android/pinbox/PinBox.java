package com.baasbox.android.pinbox;

import android.app.Application;

import com.baasbox.android.BAASBox;

/**
 * Created by eto on 09/01/14.
 */
public class PinBox extends Application {

    //todo global unique baasbox client
    private static BAASBox box;

    @Override
    public void onCreate() {
        super.onCreate();
        //todo global unique baasbox client
        box = BAASBox.createClient(this);
    }

    //todo obtain the app client
    public static BAASBox getBaasBox() {
        return box;
    }
}

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

        BAASBox.Config config = new BAASBox.Config();
        config.HTTPS = false;
        config.API_DOMAIN = "pinboxapi.baasbox.com";
        config.APP_CODE = "123PinBox456";

        //todo global unique baasbox client
        box = BAASBox.createClient(this, config);
    }

    //todo obtain the app client
    public static BAASBox getBaasBox() {
        return box;
    }
}

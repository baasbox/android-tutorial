package com.baasbox.android.pinbox;

import android.app.Application;
import com.baasbox.android.BaasBox;
import com.baasbox.android.pinbox.utils.SyncTimeManager;

/**
 * Created by eto on 09/01/14.
 */
public class PinBox extends Application {

    private static BaasBox box;
    private static SyncTimeManager syncTimeManager;

    @Override
    public void onCreate() {
        super.onCreate();

        BaasBox.Config config = new BaasBox.Config();
        config.HTTPS = false;

        config.API_DOMAIN = "pinboxapi.baasbox.com";//"192.168.56.1";
        config.APP_CODE = "123PinBox456";/*"1234567890";*/
        syncTimeManager = new SyncTimeManager(this);
        box = BaasBox.initDefault(this, config);
    }

    //todo obtain the app client
    public static BaasBox getBaasBox() {
        return box;
    }

    public static SyncTimeManager getSyncTimeManager() {
        return syncTimeManager;
    }

}

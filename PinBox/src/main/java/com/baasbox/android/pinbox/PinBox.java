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
        config.apiDomain = "192.168.56.1";//"192.168.56.1";
        config.httpPort = 9000;
        syncTimeManager = new SyncTimeManager(this);
        box = BaasBox.initDefault(this, config);
    }

    public static BaasBox getBaasBox() {
        return box;
    }

    public static SyncTimeManager getSyncTimeManager() {
        return syncTimeManager;
    }

}

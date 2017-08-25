package pl.pw.mini.mininavigator;

import android.app.Application;

/**
 * Created by Matthew on 2015-05-10.
 */
public class App extends Application {

    private static App sInstance;

    public static App get() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }
}

package dubsapp;

import dubsapp.core.abs.A;
import dubsapp.core.abs.AbstractApp;

public class App extends AbstractApp {
    private AppManDubs mManager;

    @Override
    public void onCreate()  {
        mManager = new AppManDubs();
        A.Man = mManager;
        A.App = this;
        super.onCreate();
    }

}

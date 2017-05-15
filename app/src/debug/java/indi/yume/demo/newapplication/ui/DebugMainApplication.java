package indi.yume.demo.newapplication.ui;

import com.facebook.stetho.Stetho;

import butterknife.ButterKnife;

public class DebugMainApplication extends MainApplication{
    @Override
    public void onCreate() {
        super.onCreate();

        ButterKnife.setDebug(true);

        Stetho.initializeWithDefaults(this);

        System.out.println("DebugMainApplication: is Debug mode");
    }

    @Override
    protected void inject() {
        appComponent = DaggerDebugAppComponent.builder()
                .mainAppModule(new MainAppModule(this.getApplicationContext()))
                .build();
    }
}
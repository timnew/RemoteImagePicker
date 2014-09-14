package me.timnew.remoteimagepicker;

import android.app.Application;
import de.greenrobot.event.EventBus;
import me.timnew.shared.events.Bus;
import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EApplication;

import static org.fest.reflect.core.Reflection.staticField;

@EApplication
public class RemoteImagePickerApplication extends Application {

    @Bean
    public Bus bus;

    @AfterInject
    protected void afterInject() {
        synthesizeEventBus();
    }

    private void synthesizeEventBus() {
        staticField("defaultInstance").ofType(EventBus.class).in(EventBus.class).set(bus);
    }
}

package me.timnew.shared.events;

import de.greenrobot.event.EventBus;
import org.androidannotations.annotations.EBean;

import static org.androidannotations.annotations.EBean.Scope.Singleton;
import static org.fest.reflect.core.Reflection.staticInnerClass;

@EBean(scope = Singleton)
public class Bus extends EventBus {
    public void query(Class<?> statusType) {
        try {
            Object query = staticInnerClass("Query").in(statusType).get().newInstance();
            post(query);
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}

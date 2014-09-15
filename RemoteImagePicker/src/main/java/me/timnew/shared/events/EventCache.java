package me.timnew.shared.events;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;

@EBean

public class EventCache<TEvent, TQuery> {
    @Bean
    protected Bus bus;
    private TEvent cachedEvent;

    @AfterInject
    protected void afterInject() {
        bus.register(this);
        bus.register(new EventListener<TQuery>());
    }

    private class EventListener<T> {
        public void onEvent(TQuery query) {
            bus.post(cachedEvent);
        }
    }

    public void onEvent(TEvent event) {
        this.cachedEvent = event;
    }
}

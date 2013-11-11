package de.iweinzierl.passsafe.gui.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EventBus {

    private static EventBus instance;

    private Multimap<EventType, EventListener> eventListeners;

    private EventBus() {
        eventListeners = ArrayListMultimap.create();
    }

    public static EventBus getInstance() {
        if (instance == null) {
            instance = new EventBus();
        }

        return instance;
    }

    public void register(final EventType type, final EventListener listener) {
        if (type != null && listener != null) {
            eventListeners.put(type, listener);
        }
    }

    public void fire(final Event event) {
        if (event != null) {
            for (EventListener listener : eventListeners.get(event.getType())) {
                listener.notify(event);
            }
        }
    }
}

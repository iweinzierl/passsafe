package de.iweinzierl.passsafe.shared.data;

import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public interface DataSourceChangedListener {

    public static class DataSourceChangedEvent {
        private Object object;

        public DataSourceChangedEvent(Object object) {
            this.object = object;
        }

        public Object getObject() {
            return object;
        }
    }

    void onEntryAdded(EntryCategory category, Entry entry);
}

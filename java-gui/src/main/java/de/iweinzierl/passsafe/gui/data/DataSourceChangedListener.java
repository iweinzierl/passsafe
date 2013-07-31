package de.iweinzierl.passsafe.gui.data;

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

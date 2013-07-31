package de.iweinzierl.passsafe.gui.data;

import java.util.List;


public interface EntryDataSource {

    int getItemCount(EntryCategory category);

    List<EntryCategory> getCategories();

    List<Entry> getAllEntries(EntryCategory category);

    Entry getEntry(EntryCategory category, int index);

    void addEntry(EntryCategory category, Entry entry);

    void setDataSourceChangedListener(DataSourceChangedListener listener);

    void removeEntry(Entry entry);

    void removeCategory(EntryCategory category);

    void close();
}

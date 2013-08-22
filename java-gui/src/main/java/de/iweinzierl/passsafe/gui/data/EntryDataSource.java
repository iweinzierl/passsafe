package de.iweinzierl.passsafe.gui.data;

import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

import java.util.List;


public interface EntryDataSource {

    int getItemCount(EntryCategory category);

    List<EntryCategory> getCategories();

    List<Entry> getAllEntries(EntryCategory category);

    Entry getEntry(EntryCategory category, int index);

    Entry addEntry(EntryCategory category, Entry entry);

    void setDataSourceChangedListener(DataSourceChangedListener listener);

    void removeEntry(Entry entry);

    EntryCategory addCategory(EntryCategory category);

    void removeCategory(EntryCategory category);

    void close();
}

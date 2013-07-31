package de.iweinzierl.passsafe.gui.event;

import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;

public interface RemovedListener {

    void onEntryRemoved(Entry entry);

    void onCategoryRemoved(EntryCategory category);
}

package de.iweinzierl.passsafe.gui.event;

import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public interface RemovedListener {

    void onEntryRemoved(Entry entry);

    void onCategoryRemoved(EntryCategory category);
}

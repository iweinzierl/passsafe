package de.iweinzierl.passsafe.gui.widget.tree;

import de.iweinzierl.passsafe.shared.domain.Entry;

import javax.swing.tree.DefaultMutableTreeNode;


public class EntryNode extends DefaultMutableTreeNode implements EntryListNode {

    private Entry entry;


    public EntryNode(Entry entry) {
        super(entry.getTitle());
        this.entry = entry;
    }


    public Entry getEntry() {
        return entry;
    }
}

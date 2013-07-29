package de.iweinzierl.passsafe.gui.widget.tree;

import de.iweinzierl.passsafe.gui.data.Entry;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;


public class EntryNode extends DefaultMutableTreeNode {

    private Entry entry;


    public EntryNode(Entry entry) {
        super(entry.getTitle());
        this.entry = entry;
    }


    public Entry getEntry() {
        return entry;
    }
}

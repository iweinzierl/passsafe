package de.iweinzierl.passsafe.gui.widget.tree;

import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;


public class CategoryNode extends DefaultMutableTreeNode {

    private TreeNode parent;

    private List<MutableTreeNode> children;

    private EntryCategory category;


    public CategoryNode(EntryCategory category) {
        super(category.getTitle());
        this.category = category;
    }


    public EntryCategory getCategory() {
        return category;
    }
}

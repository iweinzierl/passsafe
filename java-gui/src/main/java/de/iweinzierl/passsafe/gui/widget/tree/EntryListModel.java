package de.iweinzierl.passsafe.gui.widget.tree;

import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import java.util.List;


public class EntryListModel {

    public static TreeNode create(EntryDataSource dataSource) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");

        for (EntryCategory category : dataSource.getCategories()) {
            CategoryNode categoryNode = new CategoryNode(category);
            root.add(categoryNode);

            List<Entry> entries = dataSource.getAllEntries(category);

            if (entries == null || entries.isEmpty()) {
                continue;
            }

            for (Entry entry : entries) {
                categoryNode.add(new EntryNode(entry));
            }
        }

        return root;
    }
}

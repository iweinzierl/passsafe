package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.widget.tree.CategoryNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryNode;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import java.awt.Dimension;
import java.util.List;


public class EntryList extends JTree {

    private final ApplicationController controller;
    private final Application application;


    public EntryList(ApplicationController controller, Application application, TreeModel model) {
        super(model);

        this.controller = controller;
        this.application = application;

        setSize(new Dimension(150, 300));
        setMinimumSize(new Dimension(150, 300));
    }


    public boolean addEntry(EntryCategory category, Entry entry) {
        TreeNode root = (TreeNode) getModel().getRoot();

        for (int i = 0; i < root.getChildCount(); i++) {
            Object child = root.getChildAt(i);

            if (child instanceof CategoryNode && ((CategoryNode) child).getCategory().getTitle()
                    .equals(category.getTitle())) {

                CategoryNode parent = (CategoryNode) child;
                EntryNode newChild = new EntryNode(entry);

                DefaultTreeModel model = (DefaultTreeModel) getModel();
                model.insertNodeInto(newChild, parent, parent.getChildCount());

                treeDidChange();
                return true;
            }
        }

        return false;
    }

    public static EntryList create(ApplicationController controller, Application parent, EntryDataSource dataSource) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        DefaultTreeModel model = new DefaultTreeModel(root);

        EntryList tree = new EntryList(controller, parent, model);

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

        return tree;
    }
}

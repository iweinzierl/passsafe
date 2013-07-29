package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.widget.tree.CategoryNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryNode;

import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import java.awt.Dimension;


public class EntryList extends JTree {

    private final ApplicationController controller;
    private final Application application;


    public EntryList(ApplicationController controller, Application application, TreeNode root) {
        super(root);

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

                ((CategoryNode) child).add(new EntryNode(entry));
                treeDidChange();
                return true;
            }
        }

        return false;
    }
}

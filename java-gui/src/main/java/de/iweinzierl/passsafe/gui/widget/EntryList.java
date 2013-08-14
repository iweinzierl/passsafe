package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.data.Entry;
import de.iweinzierl.passsafe.gui.data.EntryCategory;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.widget.tree.CategoryNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryListNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryNode;
import de.iweinzierl.passsafe.gui.widget.tree.RemoveItemMenu;

import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.Dimension;
import java.util.ArrayList;
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

        initialize();
        addTreeSelectionListener(controller);
    }

    private void initialize() {
        JPopupMenu menu = getComponentPopupMenu();
        if (menu == null) {
            menu = new JPopupMenu(Messages.getMessage(Messages.ENTRYLIST_CATEGORIES));
            setComponentPopupMenu(menu);
        }

        menu.add(new RemoveItemMenu(this, controller));
    }


    public boolean addEntry(EntryCategory category, Entry entry) {
        TreeNode root = (TreeNode) getModel().getRoot();

        for (int i = 0; i < root.getChildCount(); i++) {
            Object child = root.getChildAt(i);

            if (child instanceof CategoryNode && ((CategoryNode) child).getCategory().getTitle().equals(
                    category.getTitle())) {

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

    public void addCategory(EntryCategory category) {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        TreeNode root = (TreeNode) getModel().getRoot();

        model.insertNodeInto(new CategoryNode(category), (MutableTreeNode) root, root.getChildCount());
    }

    public void removeEntry(Entry entry) {
        MutableTreeNode root = (MutableTreeNode) getModel().getRoot();
        if (removeEntry(entry, root)) {
            ((DefaultTreeModel) getModel()).reload(root);
            expandCategories(this);
        }
    }

    private boolean removeEntry(Entry entry, MutableTreeNode node) {

        for (int i = node.getChildCount() - 1; i >= 0; i--) {

            MutableTreeNode child = (MutableTreeNode) node.getChildAt(i);
            if (child instanceof EntryNode) {

                Entry current = ((EntryNode) child).getEntry();
                if (current.getTitle().equals(entry.getTitle())) {
                    node.remove(child);
                    return true;
                }
            }
            else if (child instanceof CategoryNode) {
                if (removeEntry(entry, child)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void removeCategory(EntryCategory category) {
        MutableTreeNode root = (MutableTreeNode) getModel().getRoot();
        if (removeCategory(category, root)) {
            ((DefaultTreeModel) getModel()).reload(root);
            expandCategories(this);
        }
    }

    private boolean removeCategory(EntryCategory category, MutableTreeNode node) {

        for (int i = node.getChildCount() - 1; i >= 0; i--) {

            MutableTreeNode child = (MutableTreeNode) node.getChildAt(i);
            if (child instanceof CategoryNode) {

                EntryCategory current = ((CategoryNode) child).getCategory();
                if (current.getTitle().equals(category.getTitle())) {
                    node.remove(child);
                    return true;
                }
            }
            else if (child instanceof CategoryNode) {
                if (removeCategory(category, child)) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<EntryListNode> getSelection() {
        TreeSelectionModel selectionModel = getSelectionModel();
        TreePath[] selectionPaths = selectionModel.getSelectionPaths();

        List<EntryListNode> selection = new ArrayList<>(selectionPaths.length);
        for (TreePath path : selectionPaths) {
            Object obj = path.getLastPathComponent();

            if (obj instanceof EntryListNode) {
                selection.add((EntryListNode) obj);
            }
        }

        return selection;
    }

    public static EntryList create(ApplicationController controller, Application parent, EntryDataSource dataSource) {
        DefaultTreeModel model = new DefaultTreeModel(createRootNode(dataSource));
        EntryList tree = new EntryList(controller, parent, model);
        expandCategories(tree);

        return tree;
    }

    public static TreeNode createRootNode(EntryDataSource dataSource) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages.getMessage(Messages.ENTRYLIST_ROOTNODE));

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

    public static void expandCategories(JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        tree.expandPath(new TreePath(root));

        for (int i = 0; i < root.getChildCount(); i++) {
            tree.expandPath(new TreePath(new Object[]{root, root.getChildAt(i)}));
        }
    }
}

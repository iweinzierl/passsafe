package de.iweinzierl.passsafe.gui.widget;

import java.awt.Dimension;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import de.iweinzierl.passsafe.gui.Application;
import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.widget.tree.CategoryNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryListNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryNode;
import de.iweinzierl.passsafe.gui.widget.tree.RemoveItemMenu;
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;
import de.iweinzierl.passsafe.shared.domain.Entry;
import de.iweinzierl.passsafe.shared.domain.EntryCategory;

public class EntryList extends JTree {

    public static final Logger LOGGER = LoggerFactory.getLogger(EntryList.class);

    private final ApplicationController controller;
    private final Application application;

    public EntryList(final ApplicationController controller, final Application application, final TreeModel model) {
        super(model);

        this.controller = controller;
        this.application = application;

        setSize(new Dimension(150, 300));
        setMinimumSize(new Dimension(150, 300));

        initialize();
        setDragEnabled(true);
        setDropMode(DropMode.INSERT);
        setTransferHandler(new EntryListTransferHandler(controller));
        addTreeSelectionListener(controller);
    }

    public boolean addEntry(final EntryCategory category, final Entry entry) {
        TreeNode root = (TreeNode) getModel().getRoot();

        for (int i = 0; i < root.getChildCount(); i++) {
            Object child = root.getChildAt(i);

            if (child instanceof CategoryNode
                    && ((CategoryNode) child).getCategory().getTitle().equals(category.getTitle())) {

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

    public void addCategory(final EntryCategory category) {
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        TreeNode root = (TreeNode) getModel().getRoot();

        model.insertNodeInto(new CategoryNode(category), (MutableTreeNode) root, root.getChildCount());
    }

    public void removeEntry(final Entry entry) {
        MutableTreeNode root = (MutableTreeNode) getModel().getRoot();
        if (removeEntry(entry, root)) {
            ((DefaultTreeModel) getModel()).reload(root);
            expandCategories(this);
        }
    }

    public void removeCategory(final EntryCategory category) {
        MutableTreeNode root = (MutableTreeNode) getModel().getRoot();
        if (removeCategory(category, root)) {
            ((DefaultTreeModel) getModel()).reload(root);
            expandCategories(this);
        }
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

    public static EntryList create(final ApplicationController controller, final Application parent,
            final PassSafeDataSource dataSource) {
        DefaultTreeModel model = new DefaultTreeModel(createRootNode(dataSource));
        EntryList tree = new EntryList(controller, parent, model);
        expandCategories(tree);

        return tree;
    }

    public static void expandTree(final JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        tree.expandPath(new TreePath(root));

        for (int i = 0; i < root.getChildCount(); i++) {
            tree.expandPath(new TreePath(new Object[] {root, root.getChildAt(i)}));
        }

    }

    public static void expandCategories(final JTree tree) {
        TreeNode root = (TreeNode) tree.getModel().getRoot();

        tree.expandPath(new TreePath(root));

        /* XXX do not expand all categories as there might be a lot of entries in it; prefer category selection
         * and all entries are displayed at the entry box
         *
         * for (int i = 0; i < root.getChildCount(); i++) {
         *  tree.expandPath(new TreePath(new Object[]{root, root.getChildAt(i)}));
         * }
         */
    }

    public void filter(final PassSafeDataSource dataSource, String searchText) {
        if (searchText == null) {
            searchText = "";
        }

        LOGGER.debug("Filter entry tree with: {}", searchText);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) getModel().getRoot();
        root.removeAllChildren();
        ((DefaultTreeModel) getModel()).reload(root);

        addTreeContent(dataSource, searchText, root);
        ((DefaultTreeModel) getModel()).reload(root);

        expandTree(this);
    }

    private void initialize() {
        JPopupMenu menu = getComponentPopupMenu();
        if (menu == null) {
            menu = new JPopupMenu(Messages.getMessage(Messages.ENTRYLIST_CATEGORIES));
            setComponentPopupMenu(menu);
        }

        menu.add(new RemoveItemMenu(this, controller));
    }

    private boolean removeEntry(final Entry entry, final MutableTreeNode node) {

        for (int i = node.getChildCount() - 1; i >= 0; i--) {

            MutableTreeNode child = (MutableTreeNode) node.getChildAt(i);
            if (child instanceof EntryNode) {

                Entry current = ((EntryNode) child).getEntry();
                if (current.getTitle().equals(entry.getTitle())) {
                    node.remove(child);
                    return true;
                }
            } else if (child instanceof CategoryNode) {
                if (removeEntry(entry, child)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean removeCategory(final EntryCategory category, final MutableTreeNode node) {

        for (int i = node.getChildCount() - 1; i >= 0; i--) {

            MutableTreeNode child = (MutableTreeNode) node.getChildAt(i);
            if (child instanceof CategoryNode) {

                EntryCategory current = ((CategoryNode) child).getCategory();
                if (current.getTitle().equals(category.getTitle())) {
                    node.remove(child);
                    return true;
                }
            } else if (child instanceof CategoryNode) {
                if (removeCategory(category, child)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static TreeNode createRootNode(final PassSafeDataSource dataSource) {
        return createRootNode(dataSource, null);
    }

    public static TreeNode createRootNode(final PassSafeDataSource dataSource, final String filterText) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(Messages.getMessage(Messages.ENTRYLIST_ROOTNODE));
        addTreeContent(dataSource, filterText, root);

        return root;
    }

    private static void addTreeContent(final PassSafeDataSource dataSource, final String filterText,
            final DefaultMutableTreeNode root) {

        LOGGER.debug("addTreeContent filtered with: {}", filterText);

        for (EntryCategory category : dataSource.getCategories()) {
            CategoryNode categoryNode = new CategoryNode(category);
            root.add(categoryNode);

            List<Entry> entries = dataSource.getAllEntries(category);

            if (entries == null || entries.isEmpty()) {
                continue;
            }

            for (Entry entry : entries) {
                if (!Strings.isNullOrEmpty(filterText)
                        && entry.getTitle().toLowerCase().contains(filterText.toLowerCase())) {

                    categoryNode.add(new EntryNode(entry));
                } else if (Strings.isNullOrEmpty(filterText)) {
                    categoryNode.add(new EntryNode(entry));
                }
            }

            if (!Strings.isNullOrEmpty(filterText) && categoryNode.getChildCount() == 0) {
                categoryNode.removeFromParent();
            }
        }
    }
}

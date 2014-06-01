package de.iweinzierl.passsafe.gui.widget;

import de.iweinzierl.passsafe.gui.ApplicationController;
import de.iweinzierl.passsafe.gui.widget.tree.CategoryNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryListNode;
import de.iweinzierl.passsafe.gui.widget.tree.EntryListTransferable;
import de.iweinzierl.passsafe.gui.widget.tree.EntryNode;
import de.iweinzierl.passsafe.shared.domain.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.List;

public class EntryListTransferHandler extends TransferHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntryListTransferHandler.class);

    private ApplicationController controller;

    public EntryListTransferHandler(ApplicationController controller) {
        this.controller = controller;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return MOVE;
    }

    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action) {
        if (!(comp instanceof EntryList)) {
            return;
        }

        super.exportAsDrag(comp, e, action);
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        if (c instanceof EntryList) {
            List<EntryListNode> selection = ((EntryList) c).getSelection();
            return new EntryListTransferable(selection);
        }

        return null;
    }

    @Override
    public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
        for (DataFlavor flavor : transferFlavors) {
            if (flavor.getRepresentationClass() == EntryList.class) {
                return true;
            }

            LOGGER.warn("Unsupported representation class: {}", flavor.getRepresentationClass());
        }

        return false;
    }

    @Override
    public boolean importData(JComponent comp, Transferable t) {
        try {
            final EntryListTransferable transferData = (EntryListTransferable) t.getTransferData(
                    EntryListTransferable.FLAVOR);

            final EntryList entryList = (EntryList) comp;
            final List<EntryListNode> selection = transferData.getSelection();

            doImport(entryList, entryList.getDropLocation().getPath(), selection);
            entryList.updateUI();
    } catch (UnsupportedFlavorException e) {
        e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void doImport(EntryList entryList, TreePath path, List<EntryListNode> selection) {
        EntryListNode lastPathComponent = (EntryListNode) path.getLastPathComponent();
        if (lastPathComponent instanceof CategoryNode) {
            CategoryNode category = ((CategoryNode) lastPathComponent);

            for (EntryListNode entry : selection) {
                importEntry(entryList, category, entry);
            }
        }
    }

    private void importEntry(EntryList entryList, CategoryNode category, EntryListNode entryListNode) {
        if (!(entryListNode instanceof EntryNode)) {
            LOGGER.warn("Unsupported import of {}", entryListNode.getClass());
            return;
        }

        EntryNode entryNode = (EntryNode) entryListNode;
        Entry entry = entryNode.getEntry();

        LOGGER.info("Import {} to {}", entry.getTitle(), category.getCategory().getTitle());
        controller.getDataSource().updateEntryCategory(entry, category.getCategory());

        ((DefaultTreeModel) entryList.getModel()).removeNodeFromParent(entryNode);
        ((DefaultTreeModel) entryList.getModel()).insertNodeInto(entryNode, category, 0);
        ((DefaultTreeModel) entryList.getModel()).reload(entryNode.getRoot());
    }
}

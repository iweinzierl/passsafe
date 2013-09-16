package de.iweinzierl.passsafe.gui.widget.tree;

import de.iweinzierl.passsafe.gui.widget.EntryList;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class EntryListTransferable implements Transferable, Serializable {

    public static final DataFlavor FLAVOR = new DataFlavor(EntryList.class, "Drag&Drop EntryList");

    private List<EntryListNode> selection;

    public EntryListTransferable(List<EntryListNode> selection) {
        this.selection = selection;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[] { FLAVOR };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        // TODO Check for correct data flavour
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return this;
        }
        else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    public List<EntryListNode> getSelection() {
        return selection;
    }
}

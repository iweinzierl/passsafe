package de.iweinzierl.passsafe.gui.widget.tree;

import de.iweinzierl.passsafe.gui.event.RemovedListener;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.Action;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class RemoveItemMenu extends JMenuItem {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveItemMenu.class);

    public RemoveItemMenu(EntryList tree, RemovedListener listener) {
        super(new ActionImpl(tree, listener));
    }


    private static class ActionImpl implements Action {

        private EntryList tree;
        private RemovedListener listener;

        private ActionImpl(EntryList tree, RemovedListener listener) {
            this.tree = tree;
            this.listener = listener;
        }

        @Override
        public Object getValue(String s) {
            switch (s) {
                case "Name":
                    return Messages.getMessage(Messages.ENTRYLIST_MENU_REMOVEITEM);
                default:
                    return null;
            }
        }

        @Override
        public void putValue(String s, Object o) {

        }

        @Override
        public void setEnabled(boolean b) {
            // nothing to do here
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        @Override
        public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {

        }

        @Override
        public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {

        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            List<EntryListNode> selection = tree.getSelection();
            for (EntryListNode node: selection) {
                if (node instanceof EntryNode) {
                    listener.onEntryRemoved(((EntryNode) node).getEntry());
                }
                else if (node instanceof CategoryNode) {
                    listener.onCategoryRemoved(((CategoryNode) node).getCategory());
                }
            }
        }
    }
}

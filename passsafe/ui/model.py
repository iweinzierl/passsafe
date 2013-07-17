from gi.repository import Gtk

class PwStoreListModelProxy(Gtk.ListStore):
    def __init__(self, data_type, pw_store):
        Gtk.ListStore.__init__(self, data_type)
        for entry in pw_store.entries:
            self.append([entry.title])

    def on_add(self, pw_entry):
        self.append([pw_entry.title])

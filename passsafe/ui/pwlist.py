from gi.repository import Gtk

class PwList(Gtk.TreeView):
    def __init__(self, store):
        super(PwList, self).__init__(store)
        renderer = Gtk.CellRendererText()
        column   = Gtk.TreeViewColumn("Passwords")
        column.pack_start(renderer, True)
        column.add_attribute(renderer, "text", 0)
        self.append_column(column)

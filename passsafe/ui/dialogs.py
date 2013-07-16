from gi.repository import Gtk

class KeyPromptDialog(Gtk.Dialog):
    def __init__(self, parent):
        Gtk.Dialog.__init__(self, "Entsperren", parent, 0,
            (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL,
            Gtk.STOCK_OK, Gtk.ResponseType.OK))

        self.key = Gtk.Entry(visibility=False)
        self._init_layout()
        self.show_all()

    def _init_layout(self):
        content_area = self.get_content_area()
        content_area.pack_start(self.key, False, True, 0)


class NewEntryDialog(Gtk.Dialog):
    def __init__(self, parent):
        Gtk.Dialog.__init__(self, "Neuer Eintrag", parent, 0,
            (Gtk.STOCK_CANCEL, Gtk.ResponseType.CANCEL,
             Gtk.STOCK_OK, Gtk.ResponseType.OK))

        self.title = Gtk.Entry()
        self.username = Gtk.Entry()
        self.password = Gtk.Entry(visibility=False)

        self._init_layout()
        self.show_all()


    def _init_layout(self):
        content_area = self.get_content_area()
        grid = Gtk.Grid()

        grid.attach(Gtk.Label("Title:"), 0, 0, 1, 1)
        grid.attach(Gtk.Label("Username:"), 0, 1, 1, 1)
        grid.attach(Gtk.Label("Password:"), 0, 2, 1, 1)

        grid.attach(self.title, 1, 0, 1, 1)
        grid.attach(self.username, 1, 1, 1, 1)
        grid.attach(self.password, 1, 2, 1, 1)

        content_area.pack_start(grid, False, True, 0)

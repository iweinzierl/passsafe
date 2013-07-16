from gi.repository import Gtk

class DetailView(Gtk.Grid):
    def __init_(self):
        super(DetailView, self).__init__()

        username_label = Gtk.Label('Username:')
        password_label = Gtk.Label('Password:')
        username = Gtk.Label()
        password = Gtk.Label()
        self.attach(username_label, 0, 0, 1, 1)
        self.attach(username, 0, 1, 1, 1)
        self.attach(password_label, 0, 1, 1, 1)
        self.attach(password, 1, 1, 1, 1)
        return grid

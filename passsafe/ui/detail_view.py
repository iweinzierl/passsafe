from gi.repository import Gtk

class DetailView(Gtk.Grid):
    def __init__(self):
        Gtk.Grid.__init__(self)

        title_label    = Gtk.Label('Title: ')
        username_label = Gtk.Label('Username: ')
        password_label = Gtk.Label('Password: ')
        self.title    = Gtk.Label("TEST TITLE")
        self.username = Gtk.Label("TEST USER")
        self.password = Gtk.Label("TEST PASS")
        self.attach(title_label, 0, 0, 1, 1)
        self.attach(self.title, 1, 0, 1, 1)
        self.attach(username_label, 0, 1, 1, 1)
        self.attach(self.username, 1, 1, 1, 1)
        self.attach(password_label, 0, 2, 1, 1)
        self.attach(self.password, 1, 2, 1, 1)

    def update(self, pw_entry):
        self.title.set_text(pw_entry.title)
        self.username.set_text(pw_entry.username)
        self.password.set_text(pw_entry.password)

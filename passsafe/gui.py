from gi.repository import Gtk

import sys
from passsafe.secure import PwProc
from passsafe.model import PwEntry
from passsafe.ui.pwlist import PwList
from passsafe.ui.detail_view import DetailView
from passsafe.ui.button_groups import create_main_button_group
from passsafe.ui.dialogs import NewEntryDialog, KeyPromptDialog
from passsafe.ui.model import PwStoreListModelProxy

class GtkGui(object):
    def __init__(self, config, pw_store):
        self.window = Gtk.Window()
        self.window.connect('delete-event', Gtk.main_quit)
        self.detail_view = DetailView()
        self.pw_store = pw_store
        self.pw_list = create_pw_list(self.pw_store)
        self.pw_list.get_selection().connect('changed', self.pw_entry_changed)
        self._init_layout()
        key = self._prompt_key()
        self.pw_proc = PwProc(key)

    def _init_layout(self):
        root_layout = Gtk.Box(orientation=Gtk.Orientation.VERTICAL)

        middle_layout = Gtk.Paned(orientation=Gtk.Orientation.HORIZONTAL)
        middle_layout.add1(self.pw_list)
        middle_layout.add2(self.detail_view)

        root_layout.pack_start(create_main_button_group(
            self.new_entry, self.delete_entry),
            False, True, 0)
        root_layout.pack_start(middle_layout, False, True, 0)

        self.window.set_default_size(400, 200)
        self.window.add(root_layout)

    def _prompt_key(self):
        dialog = KeyPromptDialog(self.window)
        response = dialog.run()
        if response == Gtk.ResponseType.OK:
            key = dialog.key.get_text()
            dialog.destroy()
            if len(key) == 0:
                return self._prompt_key()
            return key
        elif response == Gtk.ResponseType.CANCEL:
            sys.exit(0)
        else:
            dialog.destroy()
            return self._prompt_key()

    def show(self):
        self.window.show_all()
        Gtk.main()

    def save(self, title, username, password):
        encrypted = self.pw_proc.encrypt(password)
        self.pw_store.add(PwEntry(title, username, encrypted))
        return

    def new_entry(self, source):
        dialog = NewEntryDialog(self.window)
        response = dialog.run()
        if response == Gtk.ResponseType.OK:
            title    = dialog.title.get_text()
            username = dialog.username.get_text()
            password = dialog.password.get_text()
            self.save(title, username, password)
        dialog.destroy()

    def delete_entry(self, source):
        print "Show selected entry: %s" % source

    def pw_entry_changed(self, selection):
        self.detail_view.update(selection.get_selected())


def create_pw_list(pw_store):
    store = PwStoreListModelProxy(str, pw_store)
    pw_store.set_append_listener(store)
    return PwList(store)

def create_button(title, func):
    button = Gtk.Button(label=title)
    button.connect('clicked', func)
    return button


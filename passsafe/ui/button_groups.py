from gi.repository import Gtk

def create_main_button_group(add_listener, delete_listener):
    add    = Gtk.Button(label='Neu')
    remove = Gtk.Button(label='Entfernen')

    add.connect('clicked', add_listener)
    remove.connect('clicked', delete_listener)

    box = Gtk.Box(spacing=6, homogeneous=True)
    box.pack_start(add, False, True, 0)
    box.pack_start(remove, False, True, 0)
    return box


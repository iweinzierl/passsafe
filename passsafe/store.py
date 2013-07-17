# Copyright (C) 2013 by Ingo Weinzierl
# Authors:
# Ingo Weinzierl <weinzierl.ingo@gmail.com>
#
# This program is free software under the GPL (>=v3)
# Read the file LICENSE coming with the software for details.

import os
import sqlite3

from passsafe.model import PwEntry

SCHEMA = """
    CREATE TABLE pw_entry (
        id          INTEGER PRIMARY KEY AUTOINCREMENT,
        title       TEXT,
        username    TEXT,
        password    TEXT
    );
"""

SQL_SELECT_ALL_ENTRIES = """
    SELECT id, title, username, password
        FROM pw_entry;
"""

SQL_INSERT_PWENTRY = """
    INSERT INTO pw_entry (title, username, password)
        VALUES (?, ?, ?);
"""

class StoreAppendListener(object):
    def on_add(pw_entry):
        raise NotImplemented("StoreAppendListener.on_add() is not implemented")

class Store(object):
    def __init__(self, config, append_listener=None):
        self.entries = []
        self.config  = config
        self.append_listener = append_listener
        self.conn    = None
        self.cursor  = None
        self._init_from_db()

    def add(self, pw_entry):
        self.entries.append(pw_entry)
        self._persist(pw_entry)
        if self.append_listener is not None:
            self.append_listener.on_add(pw_entry)

    def set_append_listener(self, append_listener):
        self.append_listener = append_listener

    def get_by_index(self, index):
        return self.entries[index]

    def _init_db(self):
        print "Connect to '%s'" % self.config.db
        self.conn   = sqlite3.connect(self.config.db)
        self.cursor = self.conn.cursor()
        self.cursor.execute(SCHEMA)
        self.conn.close()

    def _init_from_db(self):
        if os.path.isfile(self.config.db) is False:
            self._init_db()

        self.conn   = sqlite3.connect(self.config.db)
        self.cursor = self.conn.cursor()
        self.cursor.execute(SQL_SELECT_ALL_ENTRIES)
        for row in self.cursor.fetchall():
            self.entries.append(PwEntry(row[1], row[2], row[3], id=row[0]))

    def _persist(self, pw_entry):
        self.cursor.execute(SQL_INSERT_PWENTRY, (
            pw_entry.title, pw_entry.username, pw_entry.password))
        self.conn.commit()

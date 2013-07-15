# Copyright (C) 2013 by Ingo Weinzierl
# Authors:
# Ingo Weinzierl <weinzierl.ingo@gmail.com>
#
# This program is free software under the GPL (>=v3)
# Read the file LICENSE coming with the software for details.

import sys

from passsafe.model import PwEntry
from passsafe.secure import PwProc, BLOCK_SIZE
from passsafe.store import Store

CHOICE_STR = """
Choose action:
  1) add new password
  2) get existing password
  3) list entries
  9) Quit

  Choose action: 
"""

CHOICE_ADD_PASSWORD = 1
CHOICE_GET_PASSWORD = 2
CHOICE_LIST_ENTRIES = 3
CHOICE_QUIT_APPLICATION = 9

class Cmd(object):
    def __init__(self, config):
        self.config = config
        self.store  = Store(config)
        self.secret = self._get_secret()
        self.pw_proc = PwProc(self.secret)

    def start(self):
        self._print_opts()

    def _get_secret(self):
        secret = raw_input("Unlock database with your secret: ")
        if secret is None or len(secret) == 0:
            return self._get_secret()
        elif len(secret) == 16:
            return secret * 2
        elif len(secret) == 8:
            return secret * 4
        elif len(secret) == 4:
            return secret * 8
        elif len(secret) == BLOCK_SIZE:
            return secret
        else:
            raise ValueError("Illegal secret!")

    def _print_opts(self):
        choice = input(CHOICE_STR)
        if choice == CHOICE_ADD_PASSWORD:
            self._add_password()
        elif choice == CHOICE_GET_PASSWORD:
            self._get_password()
        elif choice == CHOICE_LIST_ENTRIES:
            self._list_entries()
        elif choice == CHOICE_QUIT_APPLICATION:
            print "Goodbye :-)"
            sys.exit(0)
        else:
            self._print_opts()

    def _add_password(self):
        title  = raw_input("Enter title: ")
        user   = raw_input("Enter username: ")
        passwd = raw_input("Enter password: ")

        encrypted_password = self.pw_proc.encrypt(passwd)
        self.store.add(PwEntry(title, user, encrypted_password))
        self._print_opts()

    def _display_all_entries(self):
        index = 0
        for pw_entry in self.store.entries:
            print "[%i] %s" % (index, pw_entry.title)
            index += 1

    def _get_password(self):
        self._display_all_entries()
        index = input("Select entry: ")
        try:
            print self.pw_proc.decrypt(self.store.entries[index].password)
        except:
            print "Unable to decrypt password!"
        self._print_opts()

    def _list_entries(self):
        self._display_all_entries()
        self._print_opts()

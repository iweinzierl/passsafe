# Copyright (C) 2013 by Ingo Weinzierl
# Authors:
# Ingo Weinzierl <weinzierl.ingo@gmail.com>
#
# This program is free software under the GPL (>=v3)
# Read the file LICENSE coming with the software for details.

class PwEntry(object):
    def __init__(self, title, username, password, id=-1):
        self.id       = id
        self.title    = title
        self.username = username
        self.password = password

    def __str__(self):
        return "[id=%i, title=%s, username=%s, password=%s]" % (
            self.id,
            self.title,
            self.username,
            self.password)

#!/usr/bin/python
# Copyright (C) 2013 by Ingo Weinzierl
# Authors:
# Ingo Weinzierl <weinzierl.ingo@gmail.com>
#
# This program is free software under the GPL (>=v3)
# Read the file LICENSE coming with the software for details.

import sys
import os

from passsafe.secure import PwProc
from passsafe.config import prepare_argparser
from passsafe.cmd import Cmd

def _initialize_working_dir(config):
    wdir = config.dir
    print "Working dir is '%s'" % wdir
    if not os.path.exists(wdir):
        print "Creste working directory: '%s'" % wdir
        os.mkdir(wdir)

config = prepare_argparser()
_initialize_working_dir(config)
if config.nogui:
    Cmd(config).start()


# Copyright (C) 2013 by Ingo Weinzierl
# Authors:
# Ingo Weinzierl <weinzierl.ingo@gmail.com>
#
# This program is free software under the GPL (>=v3)
# Read the file LICENSE coming with the software for details.

import argparse
import os

def prepare_argparser():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        '--nogui',
        dest='nogui',
        action='store_const',
        const='nogui',
        default=False,
        help='Force command line mode with no gui')
    parser.add_argument(
        '--dir',
        dest='dir',
        default=os.environ['HOME'] + '/.passsafe',
        help='Specify an alternative base directory')
    parser.add_argument(
        '--config',
        dest='config',
        default='~/.passsafe/config.rc',
        help='Specify an alternative configuration file')
    parser.add_argument(
        '--db',
        dest='db',
        default='~/.passsafe/passsafe-db.sqlite',
        help='Specify an alternative sqlite3 database file')
    return parser.parse_args()

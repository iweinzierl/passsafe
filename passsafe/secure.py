# Copyright (C) 2013 by Ingo Weinzierl
# Authors:
# Ingo Weinzierl <weinzierl.ingo@gmail.com>
#
# This program is free software under the GPL (>=v3)
# Read the file LICENSE coming with the software for details.

from Crypto.Cipher import AES
from base64 import b64encode, b64decode

BLOCK_SIZE = 32

INTERRUPT_CHAR = u'\u0001'
PADDING_CHAR   = u'\u0000'

IV = u'12345678abcdefgh'

class PwProc(object):
    def __init__(self, key):
        if len(key) != BLOCK_SIZE:
            raise ValueError("Illegal key size! Must be %i" % BLOCK_SIZE)
        self.key = key

    def encrypt(self, password):
        padded = self._pad(password)
        cipher = AES.new(self.key, AES.MODE_CFB, IV)
        encrypted = cipher.encrypt(password)
        return b64encode(encrypted)

    def decrypt(self, encrypted):
        plain = b64decode(encrypted)
        cipher = AES.new(self.key, AES.MODE_CFB, IV)
        decrypted = cipher.decrypt(plain)
        return self._unpad(decrypted)

    def _pad(self, data):
        new_data = ''.join([data, INTERRUPT_CHAR])
        diff = BLOCK_SIZE - len(new_data) % BLOCK_SIZE
        if diff == 0:
            return data
        else:
            pad_string = PADDING_CHAR * diff
            return ''.join([new_data, pad_string])

    def _unpad(self, data):
        return data.rstrip(PADDING_CHAR).rstrip(INTERRUPT_CHAR)

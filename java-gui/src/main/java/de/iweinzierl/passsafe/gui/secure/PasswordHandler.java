package de.iweinzierl.passsafe.gui.secure;

import de.iweinzierl.passsafe.gui.exception.PassSafeSecurityException;

public interface PasswordHandler {

    String encrypt(String decrypted) throws PassSafeSecurityException;

    String decrypt(String encrypted) throws PassSafeSecurityException;
}

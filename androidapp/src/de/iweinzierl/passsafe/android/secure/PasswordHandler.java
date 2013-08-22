package de.iweinzierl.passsafe.android.secure;

import de.iweinzierl.passsafe.android.exception.PassSafeSecurityException;

public interface PasswordHandler {

    String encrypt(String decrypted) throws PassSafeSecurityException;

    String decrypt(String encrypted) throws PassSafeSecurityException;
}

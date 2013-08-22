package de.iweinzierl.passsafe.android.secure;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import de.iweinzierl.passsafe.android.exception.PassSafeSecurityException;
import de.iweinzierl.passsafe.android.logging.Logger;

public class AesPasswordHandler implements PasswordHandler {

    private static final Logger LOGGER = new Logger("AesPasswordHandler");

    private static final String KEY_ALGORITHM = "AES";
    private String secret;

    public AesPasswordHandler(String secret) {
        this.secret = secret;
    }

    private Key getSecretKey(String secret) {
        return new SecretKeySpec(DigestUtils.md5(secret), KEY_ALGORITHM);
    }

    @Override
    public String encrypt(String decrypted) throws PassSafeSecurityException {
        try {

            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(secret));

            byte[] bytes = cipher.doFinal(decrypted.getBytes());
            return Hex.encodeHexString(bytes);

        } catch (InvalidKeyException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (BadPaddingException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        }

        LOGGER.error("Unable to encrypt password");

        throw new PassSafeSecurityException("Unable to encrypt password. See logfiles for further details.");
    }

    @Override
    public String decrypt(String encrypted) throws PassSafeSecurityException {
        try {

            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secret));

            byte[] bytes = cipher.doFinal(Hex.decodeHex(encrypted.toCharArray()));
            return new String(bytes);


        } catch (InvalidKeyException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (BadPaddingException e) {
        } catch (NoSuchAlgorithmException e) {
        } catch (NoSuchPaddingException e) {
        } catch (DecoderException e) {
        }

        LOGGER.error("Unable to decrypt password");
        throw new PassSafeSecurityException("Unable to decrypt password. See logfiles for further details.");
    }
}

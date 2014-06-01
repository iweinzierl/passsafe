package de.iweinzierl.passsafe.gui.secure;

import de.iweinzierl.passsafe.gui.exception.PassSafeSecurityException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AesPasswordHandler implements PasswordHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AesPasswordHandler.class);

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

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException e) {

            LOGGER.error("Unable to encrypt password", e);
        }

        throw new PassSafeSecurityException("Unable to encrypt password. See logfiles for further details.");
    }

    @Override
    public String decrypt(String encrypted) throws PassSafeSecurityException {
        try {

            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(secret));

            byte[] bytes = cipher.doFinal(Hex.decodeHex(encrypted.toCharArray()));
            return new String(bytes);

        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
                | NoSuchPaddingException | DecoderException e) {

            LOGGER.error("Unable to decrypt password", e);
        }

        throw new PassSafeSecurityException("Unable to decrypt password. See logfiles for further details.");
    }
}

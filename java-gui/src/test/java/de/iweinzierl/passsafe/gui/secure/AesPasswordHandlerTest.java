package de.iweinzierl.passsafe.gui.secure;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

public class AesPasswordHandlerTest {

    private static final String TEST_SECRET = "TestSecret";
    private static final String TEST_PASSWORD = "TestPassword";

    private PasswordHandler passwordHandler;

    @Before
    public void setup() throws Exception {
        passwordHandler = new AesPasswordHandler(TEST_SECRET);
    }

    @Test
    public void testEncryptionAndDecryption() throws Exception {

        String encrypted = passwordHandler.encrypt(TEST_PASSWORD);
        Assert.assertNotNull(encrypted);

        String decrypted = passwordHandler.decrypt(encrypted);
        Assert.assertNotNull(decrypted);

        Assert.assertEquals("Decrypted password does not equal original password", TEST_PASSWORD, decrypted);
    }
}

package de.iweinzierl.passsafe.gui.configuration;

import de.iweinzierl.passsafe.gui.data.SyncType;
import junit.framework.Assert;
import org.junit.Test;

public class SyncTypeTest {

    @Test
    public void testGoogleDrive() throws Exception {
        SyncType syncType = SyncType.getBySyncName("google_drive");

        Assert.assertNotNull(syncType);
        Assert.assertEquals(SyncType.GOOGLE_DRIVE, syncType);
    }
}

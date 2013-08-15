package de.iweinzierl.passsafe.gui.sync;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.SyncType;
import de.iweinzierl.passsafe.gui.sync.gdrive.GoogleDriveSync;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SyncFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFactory.class);

    public static final Sync createSync(SyncType syncType, Configuration configuration) throws Exception {

        try {
            switch (syncType) {

                case GOOGLE_DRIVE:
                    LOGGER.info("Create Sync '{}'", SyncType.GOOGLE_DRIVE);
                    return new GoogleDriveSync(configuration);
            }
        }
        catch (Exception e) {
            LOGGER.error("Unable to create Sync instance for type '{}'", syncType);
        }

        return null;
    }
}

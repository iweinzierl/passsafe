package de.iweinzierl.passsafe.gui.sync;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.SyncType;
import de.iweinzierl.passsafe.gui.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;

public class SyncFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncFactory.class);

    public static final Sync createSync(final SyncType syncType, final Configuration configuration,
            final PassSafeDataSource dataSource) throws Exception {

        try {
            switch (syncType) {

                case GOOGLE_DRIVE :
                    LOGGER.info("Create Sync '{}'", SyncType.GOOGLE_DRIVE);
                    return new GoogleDriveSync(configuration, dataSource);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to create Sync instance for type '{}'", syncType);
        }

        return null;
    }
}

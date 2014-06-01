package de.iweinzierl.passsafe.gui.data;

public enum SyncType {

    GOOGLE_DRIVE("google_drive");

    private String syncName;

    private SyncType(String syncName) {
        this.syncName = syncName;
    }

    public static SyncType getBySyncName(String syncName) {
        for (SyncType syncType: values()) {
            if (syncType.syncName.equals(syncName)) {
                return syncType;
            }
        }

        return null;
    }
}

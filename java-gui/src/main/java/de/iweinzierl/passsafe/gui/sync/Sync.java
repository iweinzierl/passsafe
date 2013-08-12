package de.iweinzierl.passsafe.gui.sync;

import java.io.IOException;


public interface Sync {

    void sync(String filename) throws IOException;
}

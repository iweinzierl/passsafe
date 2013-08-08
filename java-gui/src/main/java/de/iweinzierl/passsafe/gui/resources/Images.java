package de.iweinzierl.passsafe.gui.resources;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.io.IOException;

public class Images {

    public static final String ENTRYVIEW_BUTTON_SAVE = "/icons/accept.png";
    public static final String ENTRYVIEW_BUTTON_EDIT = "/icons/edit.png";
    public static final String ENTRYVIEW_BUTTON_CANCEL = "/icons/cancel.png";
    public static final String ENTRYVIEW_BUTTON_VISIBLE = "/icons/show.png";
    public static final String ENTRYVIEW_BUTTON_INVISIBLE = "/icons/hide.png";

    private static Images INSTANCE = new Images();

    public static ImageIcon getImageIcon(String key) throws IOException {
        return new ImageIcon(ImageIO.read(INSTANCE.getClass().getResource(key)));
    }
}

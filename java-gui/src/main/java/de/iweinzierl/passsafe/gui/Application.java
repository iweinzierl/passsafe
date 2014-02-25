package de.iweinzierl.passsafe.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;

import java.io.IOException;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.apache.log4j.BasicConfigurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.SqliteDataSource;
import de.iweinzierl.passsafe.gui.resources.Errors;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.secure.AesPasswordHandler;
import de.iweinzierl.passsafe.gui.sync.SyncFactory;
import de.iweinzierl.passsafe.gui.util.KeyStrokeHandler;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.gui.widget.ButtonBar;
import de.iweinzierl.passsafe.gui.widget.Display;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import de.iweinzierl.passsafe.gui.widget.EntryView;
import de.iweinzierl.passsafe.gui.widget.StartupDialogBuilder;
import de.iweinzierl.passsafe.gui.widget.table.EntryTable;
import de.iweinzierl.passsafe.gui.widget.table.EntryTableModel;
import de.iweinzierl.passsafe.shared.data.PassSafeDataSource;

public class Application extends JFrame {

    public static final int DEFAULT_WIDTH = 750;
    public static final int DEFAULT_HEIGHT = 425;

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private ButtonBar buttonBar;
    private EntryList entryList;
    private Display display;

    private ApplicationController controller;

    public static void main(final String[] args) throws Exception {
        initializeLogging();
        initializeLookAndFeel();

        Configuration configuration = Configuration.parse(Configuration.DEFAULT_CONFIGURATION_FILE);

        ApplicationController controller = new ApplicationController(configuration,
                SyncFactory.createSync(configuration.getSyncType(), configuration));

        PassSafeDataSource dataSource = new SqliteDataSource(configuration.getDatabase());
        controller.setDataSource(dataSource);

        displayEnterPasswordDialog(controller, null);
    }

    private static void displayEnterPasswordDialog(final ApplicationController controller, final String errorMessage) {
        new StartupDialogBuilder(errorMessage).setActionListener(new StartupDialogBuilder.ActionListener() {

                                                      @Override
                                                      public void submitted(final String password) {
                                                          try {
                                                              start(controller, password);
                                                          } catch (Exception e) {
                                                              LOGGER.error("Unable to start PassSafe application", e);
                                                              System.exit(1);
                                                          }
                                                      }

                                                      @Override
                                                      public void canceled() {
                                                          System.exit(1);
                                                      }
                                                  }).build();
    }

    private static void start(final ApplicationController controller, final String password) throws Exception {
        controller.setPasswordHandler(new AesPasswordHandler(password));
        if (!controller.verifyPassword()) {
            displayEnterPasswordDialog(controller, Errors.getError(Errors.PASSWORD_VERIFICATION_FAILED));
            return;
        }

        try {
            controller.requestSync();

        } catch (IOException e) {
            LOGGER.error("Unable to sync PassSafe storage with sync type '{}'",
                controller.getConfiguration().getSyncType());

            UiUtils.displayError(null, Errors.getError(Errors.SYNC_FAILED));
        }

        Application app = new Application(controller);
        setupUI(controller, app);
    }

    private static void setupUI(final ApplicationController controller, final Application app) {
        ButtonBar buttonBar = new ButtonBar(controller, app);
        EntryList entryList = EntryList.create(controller, app, controller.getDataSource());
        EntryTable entryTable = new EntryTable(controller, new EntryTableModel(), controller);
        EntryView entryView = new EntryView(controller);

        Display display = new Display(controller, entryTable, entryView);

        controller.setApplication(app);
        controller.setEntryList(entryList);
        controller.setEntryTable(entryTable);
        controller.setEntryView(entryView);
        controller.setButtonBar(buttonBar);

        app.setButtonBar(buttonBar);
        app.setEntryList(entryList);
        app.setDisplay(display);

        try {
            app.initialize();
            app.setVisible(true);

        } catch (SQLException | ClassNotFoundException | IOException e) {
            LOGGER.error("Unable to initialize application", e);
            System.exit(1);
        }

        LOGGER.info("Started PassSafe Java User Interface");
    }

    public Application(final ApplicationController controller) {
        this.controller = controller;
    }

    public void initialize() throws SQLException, ClassNotFoundException, IOException {
        initializeLayout();
        initializeKeyStrokes();

        addWindowListener(controller);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Messages.getMessage(Messages.APP_TITLE));
    }

    private static void initializeLogging() {
        BasicConfigurator.configure();
    }

    private static void initializeLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {                                                         /* nothing to do */
        }
    }

    private void initializeLayout() {
        JScrollPane entryListPane = new JScrollPane(entryList);
        entryListPane.setPreferredSize(new Dimension(250, 450));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(buttonBar, BorderLayout.NORTH);
        contentPane.add(entryListPane, BorderLayout.WEST);
        contentPane.add(display, BorderLayout.CENTER);

        contentPane.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        UiUtils.center(this);
    }

    private void initializeKeyStrokes() {
        LOGGER.info("Initialize key stroke event handling");

        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher(new KeyEventDispatcher() {
                @Override
                public boolean dispatchKeyEvent(final KeyEvent e) {
                    return !e.isConsumed() && KeyStrokeHandler.getKeyStrokeHandler(controller).handle(e);

                }
            });
    }

    public void setButtonBar(final ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }

    public void setEntryList(final EntryList entryList) {
        this.entryList = entryList;
    }

    public void setDisplay(final Display display) {
        this.display = display;
    }
}

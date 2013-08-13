package de.iweinzierl.passsafe.gui;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.data.SqliteDataSource;
import de.iweinzierl.passsafe.gui.resources.Messages;
import de.iweinzierl.passsafe.gui.secure.AesPasswordHandler;
import de.iweinzierl.passsafe.gui.sync.gdrive.GoogleDriveSync;
import de.iweinzierl.passsafe.gui.util.UiUtils;
import de.iweinzierl.passsafe.gui.widget.ButtonBar;
import de.iweinzierl.passsafe.gui.widget.Display;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import de.iweinzierl.passsafe.gui.widget.EntryView;
import de.iweinzierl.passsafe.gui.widget.StartupDialogBuilder;
import de.iweinzierl.passsafe.gui.widget.table.EntryTable;
import de.iweinzierl.passsafe.gui.widget.table.EntryTableModel;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.sql.SQLException;


public class Application extends JFrame {

    public static final int DEFAULT_WIDTH = 650;
    public static final int DEFAULT_HEIGHT = 400;

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private ButtonBar buttonBar;
    private EntryList entryList;
    private Display display;

    private ApplicationController controller;


    public static void main(String[] args) throws Exception {
        initializeLogging();

        new StartupDialogBuilder().setActionListener(new StartupDialogBuilder.ActionListener() {

            @Override
            public void submitted(String password) {
                try {
                    start(password);
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

    private static void start(String password) throws Exception {
        Configuration configuration = Configuration.parse(Configuration.DEFAULT_CONFIGURATION_FILE);

        ApplicationController controller = new ApplicationController(configuration, new AesPasswordHandler(password),
                new GoogleDriveSync(configuration));
        Application app = new Application(controller);

        EntryDataSource dataSource = new SqliteDataSource(configuration.getDatabase());
        controller.setDataSource(dataSource);

        ButtonBar buttonBar = new ButtonBar(controller, app);
        EntryList entryList = EntryList.create(controller, app, controller.getDataSource());
        EntryTable entryTable = new EntryTable(controller, new EntryTableModel(), controller);
        EntryView entryView = new EntryView(controller);

        Display display = new Display(controller, entryTable, entryView);

        controller.setEntryList(entryList);
        controller.setEntryTable(entryTable);
        controller.setEntryView(entryView);
        controller.setButtonBar(buttonBar);

        app.setButtonBar(buttonBar);
        app.setEntryList(entryList);
        app.setDisplay(display);

        try {
            app.initialize();
            app.show();

        } catch (SQLException | ClassNotFoundException | IOException e) {
            LOGGER.error("Unable to initialize application", e);
            System.exit(1);
        }

        LOGGER.info("Started PassSafe Java User Interface");
    }


    public Application(ApplicationController controller) {
        this.controller = controller;
    }


    public void initialize() throws SQLException, ClassNotFoundException, IOException {
        initializeLayout();

        addWindowListener(controller);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle(Messages.getMessage(Messages.APP_TITLE));
    }


    private static void initializeLogging() {
        BasicConfigurator.configure();
    }


    private void initializeLayout() {
        JScrollPane entryListPane = new JScrollPane(entryList);
        entryListPane.setPreferredSize(new Dimension(200, 450));

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(buttonBar, BorderLayout.NORTH);
        contentPane.add(entryListPane, BorderLayout.WEST);
        contentPane.add(display, BorderLayout.CENTER);

        contentPane.setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_WIDTH));
        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
        setMinimumSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));

        UiUtils.center(this);
    }


    public void setButtonBar(ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }


    public void setEntryList(EntryList entryList) {
        this.entryList = entryList;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }
}

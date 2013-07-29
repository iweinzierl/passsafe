package de.iweinzierl.passsafe.gui;

import de.iweinzierl.passsafe.gui.configuration.Configuration;
import de.iweinzierl.passsafe.gui.data.EntryDataSource;
import de.iweinzierl.passsafe.gui.data.SqliteDataSource;
import de.iweinzierl.passsafe.gui.widget.ButtonBar;
import de.iweinzierl.passsafe.gui.widget.EntryList;
import de.iweinzierl.passsafe.gui.widget.tree.EntryListModel;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.sql.SQLException;


public class Application extends JFrame {

    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_HEIGHT = 400;

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private ButtonBar buttonBar;
    private EntryList entryList;
    private ApplicationController controller;


    public static void main(String[] args) throws Exception {
        initializeLogging();

        Configuration configuration = Configuration.parse(Configuration.DEFAULT_CONFIGURATION_FILE);

        ApplicationController controller = new ApplicationController(configuration);
        Application app = new Application(controller);

        EntryDataSource dataSource = new SqliteDataSource(configuration.getDatabase());
        controller.setDataSource(dataSource);

        ButtonBar buttonBar = new ButtonBar(controller, app);
        EntryList entryList = new EntryList(controller, app, EntryListModel.create(controller.getDataSource()));

        controller.setEntryList(entryList);
        controller.setButtonBar(buttonBar);

        app.setButtonBar(buttonBar);
        app.setEntryList(entryList);

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

        setSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
    }


    private static void initializeLogging() {
        BasicConfigurator.configure();
    }


    private void initializeLayout() {
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(buttonBar, BorderLayout.NORTH);
        contentPane.add(entryList, BorderLayout.WEST);

        contentPane.setSize(new Dimension(500, 500));
    }


    public void setButtonBar(ButtonBar buttonBar) {
        this.buttonBar = buttonBar;
    }


    public void setEntryList(EntryList entryList) {
        this.entryList = entryList;
    }
}

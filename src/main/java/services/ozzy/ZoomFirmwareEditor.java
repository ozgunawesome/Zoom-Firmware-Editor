package services.ozzy;

import org.apache.commons.cli.*;
import services.ozzy.zoomeditor.controller.ApplicationController;
import services.ozzy.zoomeditor.model.Firmware;
import services.ozzy.zoomeditor.service.FirmwareService;
import services.ozzy.zoomeditor.service.PatchService;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZoomFirmwareEditor {

    private static final String CONFIG_FILE = "app.config";
    private static final String MESSAGES_FILE = "messages";
    private static Properties properties;
    private static ResourceBundle messages;
    private static final Logger log = Logger.getLogger(ZoomFirmwareEditor.class.getName());

    private static final Options options = new Options();

    static {
        options.addOption("x", "extractall", false, "Extract all files");
        options.addOption("o", "output", true, "Use this directory as output (default = working dir)");
        options.addOption("f", "file", true, "Firmware file");
        options.addOption("h", "help", false, "Print this help");
    }

    private static void printHelpAndExit() {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp("tool.jar", "Zoom Pedal Firmware Utility", options, null, true);
        System.exit(0);
    }

    public static void main(String[] args) {

        System.setProperty("java.util.logging.SimpleFormatter.format", "%1$tF %1$tT %4$s %2$s(): %5$s%6$s%n"); // set log output into single line
        try {
            properties = new Properties();
            properties.load(ZoomFirmwareEditor.class.getClassLoader().getResourceAsStream(CONFIG_FILE));
            messages = ResourceBundle.getBundle(MESSAGES_FILE, Locale.ROOT);
        } catch (IOException e) {
            e.printStackTrace();
            log.log(Level.SEVERE, e.getMessage(), e);
            return;
        }

        if (args.length > 0) {
            log.info("Running in console mode");

            CommandLineParser parser = new DefaultParser();
            try {
                CommandLine cmd = parser.parse(options, args);
                File file = null;

                if (cmd.hasOption('h')) {
                    printHelpAndExit();
                }

                if (cmd.hasOption('f')) {
                    file = new File(cmd.getOptionValue('f'));
                }

                String outputDir = cmd.hasOption('o') ? cmd.getOptionValue('o') : System.getProperty("user.dir");

                if (cmd.hasOption('x')) {
                    if (file == null) {
                        log.severe("No file specified.");
                        printHelpAndExit();
                    }
                    FirmwareService firmwareService = FirmwareService.getInstance();
                    Firmware firmware = firmwareService.initFirmware(file);
                    PatchService patchService = PatchService.getInstance();
                    boolean result = patchService.saveAllPatchFiles(firmware, outputDir);
                    if (result) {
                        log.info("Complete");
                    } else {
                        log.severe("Complete with errors!");
                    }
                    return;
                }

            } catch (ParseException e) {
                log.log(Level.SEVERE, e.getMessage(), e);
                return;
            }

        }

        if ("true".equalsIgnoreCase(getProperty("useWindowsLookAndFeel"))) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (Exception e) {
                log.info(e.getMessage());
                properties.setProperty("useWindowsLookAndFeel", "false");
            }
        }

        SwingUtilities.invokeLater(ApplicationController::createAndShowGUI);
    }

    public static String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val == null) ? defaultValue : val;
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getMessage(String key) {
        return messages.getString(key);
    }

}

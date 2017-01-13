package dk.paperstreet.gallerygenerator.imageprocessing;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private final OptionSet options;

    public static void main(String... args) {
        configureLogging();
        new Main(args).generate();
    }

    public Main(String... args) {
        options = parseOptions(args);
    }

    public void generate() {
        if (!Files.isDirectory(Paths.get((String) options.valueOf("output")))) {
            try {
                Files.createDirectory(Paths.get((String) options.valueOf("output")));
            } catch (IOException e) {
                System.err.println("Unable to create directory: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
        }

        ImageResizer imageResizer = new ImageResizer();
        try {
            imageResizer.resizeAllImages((String) options.valueOf("input"), (String) options.valueOf("output"), (Integer) options.valueOf("resizeto"));
        } catch (Exception e) {
            System.err.println("Unable to perform resize operation: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private OptionSet parseOptions(String... args) {
        OptionParser optionParser = new OptionParser();
        try {
            optionParser.accepts("input", "Input directory.").withRequiredArg().required();
            optionParser.accepts("output", "Output directory.").withRequiredArg().required();
            optionParser.accepts("resizeto", "Resize in pixels to this width or height, depending on which is widest.").withRequiredArg().ofType(Integer.class).required();
            OptionSet options = optionParser.parse(args);

            if (!Files.isDirectory(Paths.get((String) options.valueOf("input")))) {
                throw new IllegalArgumentException("Input directory not found: " + options.valueOf("input"));
            }

            return options;
        } catch (OptionException | IllegalArgumentException e) {
            System.err.println("Error parsing options: " + e.getMessage() + System.lineSeparator());
            try {
                optionParser.printHelpOn(System.out);
                System.exit(1);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    private static void configureLogging() {
        System.setProperty("org.slf4j.simpleLogger.logFile", "System.out");
        System.setProperty("org.slf4j.simpleLogger.showShortLogName", "true");
        System.setProperty("org.slf4j.simpleLogger.showThreadName", "false");
    }
}
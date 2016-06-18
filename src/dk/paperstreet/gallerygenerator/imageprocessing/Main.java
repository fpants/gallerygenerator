package dk.paperstreet.gallerygenerator;

import dk.paperstreet.gallerygenerator.imageprocessing.ImageResizer;
import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    private final OptionSet options;

    public static void main(String... args) {
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
                e.printStackTrace();
                // TODO: 18-06-2016
            }
        }

        ImageResizer imageResizer = new ImageResizer();
        try {
            imageResizer.resizeAllImages((String) options.valueOf("input"), (String) options.valueOf("output"), (Integer) options.valueOf("resizeto"));
        } catch (Exception e) {
            e.printStackTrace();
            // TODO: 18-06-2016
        }
    }

    private OptionSet parseOptions(String... args) {
        OptionParser optionParser = null;
        try {
            optionParser = new OptionParser();
            optionParser.accepts("input", "Input directory.").withRequiredArg().required();
            optionParser.accepts("output", "Output directory.").withRequiredArg().required();
            optionParser.accepts("resizeto", "Resize in pixels to this width or height, depending on which is widest.").withRequiredArg().ofType(Integer.class).required();
            OptionSet options = optionParser.parse(args);

            if (!Files.isDirectory(Paths.get((String) options.valueOf("input")))) {
                throw new IllegalArgumentException("Input directory not found: " + options.valueOf("input"));
            }

            return options;
        } catch (OptionException | IllegalArgumentException e) {
            System.out.println("Error parsing options: " + e.getMessage() + System.lineSeparator());
            try {
                optionParser.printHelpOn(System.out);
                System.exit(1);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }
}
package dk.paperstreet.gallerygenerator.imageprocessing;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class ImageResizer {
    private static final Logger LOG = LoggerFactory.getLogger(ImageResizer.class);

    public void resizeAllImages(String inputDirectory, String outputDirectory, int imageLength) {
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(inputDirectory))) {
            for (Path path : directoryStream) {
                String filename = path.getFileName().toString();
                LOG.info("{}: Reading into memory from: {}", filename, path);

                BufferedImage originalImage = ImageIO.read(path.toFile());
                if (originalImage == null) {
                    LOG.info("{}: Not an image file - skipping", filename);
                    continue;
                }

                Scalr.Rotation rotation = determineRotation(path);
                if (rotation != null) {
                    LOG.info("{}: Rotating {}", filename, rotation);
                    originalImage = Scalr.rotate(originalImage, rotation);
                }

                LOG.info("{}: Resizing to {}px", filename, imageLength);
                BufferedImage resizedImage = Scalr.resize(originalImage, imageLength);

                String format = determineFormat(filename);
                String outputFilename = outputDirectory + FileSystems.getDefault().getSeparator() + path.getFileName();
                LOG.info("{}: Writing new image to: {}", filename, outputFilename);
                ImageIO.write(resizedImage, format, new File(outputFilename));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Scalr.Rotation determineRotation(Path image) {
        try {
            Metadata metadata = ImageMetadataReader.readMetadata(image.toFile());
            ExifIFD0Directory exifIFD0 = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);

            int orientation = exifIFD0.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            switch (orientation) {
                case 1: // [Exif IFD0] Orientation - Top, left side (Horizontal / normal)
                    return null;
                case 6: // [Exif IFD0] Orientation - Right side, top (Rotate 90 CW)
                    return Scalr.Rotation.CW_90;
                case 3: // [Exif IFD0] Orientation - Bottom, right side (Rotate 180)
                    return Scalr.Rotation.CW_180;
                case 8: // [Exif IFD0] Orientation - Left side, bottom (Rotate 270 CW)
                    return Scalr.Rotation.CW_270;
                default:
                    throw new RuntimeException("Unable to determine orientation of: " + image);
            }
        } catch (MetadataException | IOException | ImageProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String determineFormat(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filename.substring(lastDotIndex + 1);
        }
        throw new RuntimeException("Unable to determine file format of: " + filename);
    }
}

Simple image gallery generator
==============================
Do you often bumble into the very specific use case of having a directory filled with images that you want to put on a web server? This is for you!

This piece of software takes care of rotating, resizing, and filtering out non-image files.

Rotation is applied based on the EXIF data in the source image.

Supported file formats
----------------------
All image formats supported by the [javax.imageio library](http://docs.oracle.com/javase/8/docs/api/javax/imageio/package-summary.html#package.description) (jpeg, png, etc).

RAW files are not supported.

Usage
-----
Start by downloading the latest release from the Releases page. Then execute the java archive:

    java -jar gallerygenerator.jar --input /your/source/images --output /destination/gallery --resizeto 1000

Here are the CLI options:

    Option (* = required)   Description
    ---------------------   -----------
    * --input <String>      Input directory.
    * --output <String>     Output directory.
    * --resizeto <Integer>  Resize in pixels to this width or height, depending on
                              which is widest.

Download
--------
See the Releases page.
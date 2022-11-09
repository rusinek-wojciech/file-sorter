package org.ikinsure.filesorter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

/**
 * Program moves content from source to destination and sorts by last modified date
 */
public class FileSorter {

    private static final String SEPARATOR = FileSystems.getDefault().getSeparator();

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Missing program arguments: source and destination paths!");
            return;
        }

        final String source = args[0];
        final String destination = args[1];

        Set<Path> files = listOfFiles(source);
        files.forEach(f -> {
            try {

                BasicFileAttributes attributes = Files.readAttributes(f, BasicFileAttributes.class);
                LocalDateTime time = attributes.lastModifiedTime()
                        .toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();

                final int year = time.getYear();
                final int month = time.getMonthValue();

                System.out.println(f.getFileName() + "  " + year + "-" + month);
                Path target = Paths.get(destination + SEPARATOR + year + SEPARATOR + month);

                if (!Files.exists(target)) {
                    Files.createDirectories(target);
                }

                Files.move(f, Paths.get(target + SEPARATOR + f.getFileName()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.println("Success!");
    }

    public static Set<Path> listOfFiles(String dir) throws IOException {
        Set<Path> files = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    files.add(path);
                }
            }
        }
        return files;
    }
}

package cn.edu.yali.compiler.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * Tools for convenient file reading and writing
 */
public final class FileUtils {
    /**
     * Reads a text file and returns the file content as a String
     *
     * @param path Text file path
     * @return Text content
     */
    public static String readFile(String path) {
        return String.join("\n", readLines(path));
    }

    /**
     * Read a text file and return the file content in the form of {@code ArrayList<String>} by line
     *
     * @param path Text file path
     * @return Text content
     */
    public static List<String> readLines(String path) {
        try (final var lines = Files.lines(Paths.get(path))) {
            return lines.toList();
        } catch (IOException e) {
            throw new RuntimeException("IO Exception on " + path, e);
        }
    }

    /**
     * Write the contents to the specified file
     *
     * @param path    The path of the file to write
     * @param content What to write
     */
    public static void writeFile(String path, String content) {
        writeLines(path, List.of(content));
    }

    public static void writeLines(String path, List<String> lines) {
        try {
            Files.write(Paths.get(path), lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("IO Exception for " + path);
        }
    }

    /**
     * Create an empty file
     *
     * @param path File Path
     */
    public static void tryCreateEmptyFile(String path) {
        try {
            Files.createFile(Paths.get(path));
        } catch (FileAlreadyExistsException e) {
            throw new RuntimeException("File already exist for " + path, e);
        } catch (IOException e) {
            throw new RuntimeException("IO Exception for " + path, e);
        }
    }

    public static List<List<String>> readCSV(String path) {
        return readLines(path).stream()
            // When limit is 0 (which is the case when calling split without a limit parameter)
            // split will ignore trailing blanks, but limit=-1 will not.
            // This is critical for csv, because csv often has blank trailing cells in each row
            .map(line -> line.split(",", -1))
            .map(Arrays::asList)
            .toList();
    }

    private FileUtils() {
    }
}

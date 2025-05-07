import java.io.*;
import java.util.*;

public class FileHandling {

    private BufferedReader reader;
    private String filePath;

    // Constructor to initialize the file path
    public FileHandling(String filePath) {
        this.filePath = filePath;
    }

    // Method to open the file
    public void openFile() {
        try {
            reader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            System.err.println("Error opening file: " + filePath);
        }
    }


    public String readFile() {
        String line = null;
        try {
            if ((line = reader.readLine()) != null) {
                    return line;
            }
        } catch (IOException e) {
            System.err.println("Error reading line: ");
        }
        return null;
    }


    public int countLines() {
        int linesCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) {
                linesCount++;
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return linesCount;
    }



    // Method to read the entire file
//    public List<String> readFile() {
//        List<String> fileContent = new ArrayList<>();
//        String line;
//        try {
//            // Read the file line by line and store it in the list
//            while ((line = reader.readLine()) != null) {
//                fileContent.add(line);
//            }
//        } catch (IOException e) {
//            System.err.println("Error reading the file: " + filePath);
//        }
//        return fileContent;
//    }

    // Method to close the file
    public void closeFile() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing the file.");
        }
    }



}

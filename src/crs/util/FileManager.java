package crs.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    
    private static final String DATA_DIR = "data/";
    
    static {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    public static <T> void saveToTextFile(String filename, List<T> items) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + filename))) {
            for (T item : items) {
                writer.println(item.toString());
            }
        } catch (IOException e) {
            System.err.println("Error saving to text file: " + e.getMessage());
        }
    }
    
    public static List<String> readFromTextFile(String filename) {
        List<String> lines = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            return lines;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading from text file: " + e.getMessage());
        }
        return lines;
    }
    
    @SuppressWarnings("unchecked")
    public static <T> List<T> loadFromBinaryFile(String filename) {
        List<T> items = new ArrayList<>();
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            return items;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            items = (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from binary file: " + e.getMessage());
        }
        return items;
    }
    
    public static <T> void saveToBinaryFile(String filename, List<T> items) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + filename))) {
            oos.writeObject(items);
        } catch (IOException e) {
            System.err.println("Error saving to binary file: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T loadObjectFromBinaryFile(String filename) {
        File file = new File(DATA_DIR + filename);
        
        if (!file.exists()) {
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading object from binary file: " + e.getMessage());
        }
        return null;
    }
    
    public static <T> void saveObjectToBinaryFile(String filename, T item) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + filename))) {
            oos.writeObject(item);
        } catch (IOException e) {
            System.err.println("Error saving object to binary file: " + e.getMessage());
        }
    }
    
    public static void appendToTextFile(String filename, String content) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DATA_DIR + filename, true))) {
            writer.println(content);
        } catch (IOException e) {
            System.err.println("Error appending to text file: " + e.getMessage());
        }
    }
    
    public static boolean fileExists(String filename) {
        return new File(DATA_DIR + filename).exists();
    }
    
    public static void deleteFile(String filename) {
        File file = new File(DATA_DIR + filename);
        if (file.exists()) {
            file.delete();
        }
    }
}

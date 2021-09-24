/**
 * DotEnv.java
 * @author Mae Morella
 * 
 * A dumb tool that parses a file called ".env" for key value pairs,
 * and uses those to shadow environmental variables.
 */

import java.util.*;
import java.io.*;
import java.util.stream.*;

public class DotEnv {
  private static Map<String, String> map;
  
  private static void initialize() {
    // Construct new map
    map = new HashMap<String, String>();
    // Read from file .env
    try (BufferedReader reader = new BufferedReader(new FileReader(".env"))) {
      // For each line in file...
      Stream<String> lines = reader.lines();
      lines.forEach(s -> {
        // If line is empty or starts with a #, skip it.
        if (s.length() == 0 || s.startsWith("#")) {
          return;
        }
        String[] parts = s.split("=");
        if (parts.length == 2) {
          // Set key and value
          map.put(parts[0].trim(), parts[1].trim());
        } else {
          System.err.println("Error in .env parsing property: " + s);
        }
      });
    } catch (FileNotFoundException e) {
      // .env file does not exist. that's ok.
      return;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static String getenv(String name) {
    // If the map is uninitialized, initialize it.
    if (map == null) {
      initialize();
    }
    // If the value isn't in the map, get the environmental variable.
    if (!map.containsKey(name)) {
      return System.getenv(name);
    } else {
      return map.get(name);
    }
  }
}
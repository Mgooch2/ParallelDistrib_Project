package util;

/**
 * DotEnv.java
 * @author Mae Morella
 * @version 2.0
 * 
 * A simple utility for parsing .env files to get key,value pairs.
 * 
 * Initialize with DotEnv.load(".env");
 * Read with DotEnv.getEnv("MY_PROPERTY")
 */
import java.util.*;
import java.io.*;

public class DotEnv {
  private static final Map<String, String> propsMap = new HashMap<String, String>(); // a map containing the <K, V> pairs stored in .env
  private static boolean initialized = false;


  public static void load(String envFileName) {
    try (BufferedReader reader = new BufferedReader(new FileReader(envFileName))) {
      // For each line in file...
      reader.lines().forEach(l -> {
        if (l.trim().length() == 0) return; // line is empty; skip.
        if (l.startsWith("#")) return; // line starts with #; skip.
        String[] parts = l.split("=", 2);
        if (parts.length == 2) {
          String key = parts[0].trim(), value = parts[1].trim();
          propsMap.put(key, value);
        } else {
          System.err.println("Error parsing " + envFileName + " - Unexpected property " + l);
        }
      });
      initialized = true;
    } catch (FileNotFoundException e) {  // file doesn't exist. that's okay.
      System.out.println("A file named " + envFileName + " could not be found");
    }
    catch (IOException e) {
      System.err.println("Error reading " + envFileName + ": " + e.getMessage());
    }
  }
  /**
   * Gets the value of the specified environment variable, first from .env, and if
   * it's not declared, then from the system. An environment variable is a
   * system-dependent external named value.
   * @returns the environmental variable, or null
   */
  public static String getEnv(String name) {
    if (!initialized) {
      throw new RuntimeException("Error: DotEnv is was not initialized with a call to DotEnv.load");
    }
    // If the value isn't in the map, get the environmental variable.
    return propsMap.getOrDefault(name, System.getenv(name));
  }
  /**
   * Gets the value of the specified environment variable, first from .env, and if
   * it's not declared, then from the system. An environment variable is a
   * system-dependent external named value.
   * @returns the environmental variable, or the default value.
   */
  public static String getEnvOrDefault(String name, String defaultValue) {
    String env = getEnv(name);
    return (env == null) ? env : defaultValue;
  }
}
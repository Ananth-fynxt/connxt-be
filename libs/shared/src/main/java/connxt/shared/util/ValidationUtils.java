package connxt.shared.util;

import java.util.Collection;
import java.util.Map;

/**
 * Utility class for checking null and empty values for common data types. Provides static methods
 * to validate strings, collections, maps, and arrays.
 *
 * <p>This class cannot be instantiated and all methods are static.
 *
 * <h3>Usage Examples:</h3>
 *
 * <pre>
 * // String validation
 * if (ValidationUtils.isNullOrEmpty(name)) {
 *     throw new IllegalArgumentException("Name cannot be null or empty");
 * }
 *
 * // Collection validation
 * if (ValidationUtils.isNotNullOrEmpty(userList)) {
 *     processUsers(userList);
 * }
 *
 * // Map validation
 * if (ValidationUtils.isNullOrEmpty(parameters)) {
 *     parameters = new HashMap<>();
 * }
 *
 * // Array validation
 * if (ValidationUtils.isNotNullOrEmpty(items)) {
 *     Arrays.sort(items);
 * }
 *
 * // Multiple object null check
 * if (ValidationUtils.isAnyNull(user, email, password)) {
 *     throw new IllegalArgumentException("All fields are required");
 * }
 * </pre>
 */
public final class ValidationUtils {

  private ValidationUtils() {
    // Utility class - prevent instantiation
  }

  /**
   * Checks if a string is null or empty (after trimming whitespace).
   *
   * @param str the string to check
   * @return true if the string is null, empty, or contains only whitespace
   */
  public static boolean isNullOrEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Checks if a string is null or empty (without trimming whitespace).
   *
   * @param str the string to check
   * @return true if the string is null or empty
   */
  public static boolean isNullOrEmptyStrict(String str) {
    return str == null || str.isEmpty();
  }

  /**
   * Checks if a string is not null and not empty (after trimming whitespace).
   *
   * @param str the string to check
   * @return true if the string is not null and contains non-whitespace characters
   */
  public static boolean isNotNullOrEmpty(String str) {
    return !isNullOrEmpty(str);
  }

  /**
   * Checks if a string is not null and not empty (without trimming whitespace).
   *
   * @param str the string to check
   * @return true if the string is not null and not empty
   */
  public static boolean isNotNullOrEmptyStrict(String str) {
    return !isNullOrEmptyStrict(str);
  }

  /**
   * Checks if a collection is null or empty.
   *
   * @param collection the collection to check
   * @return true if the collection is null or empty
   */
  public static boolean isNullOrEmpty(Collection<?> collection) {
    return collection == null || collection.isEmpty();
  }

  /**
   * Checks if a collection is not null and not empty.
   *
   * @param collection the collection to check
   * @return true if the collection is not null and contains elements
   */
  public static boolean isNotNullOrEmpty(Collection<?> collection) {
    return !isNullOrEmpty(collection);
  }

  /**
   * Checks if a map is null or empty.
   *
   * @param map the map to check
   * @return true if the map is null or empty
   */
  public static boolean isNullOrEmpty(Map<?, ?> map) {
    return map == null || map.isEmpty();
  }

  /**
   * Checks if a map is not null and not empty.
   *
   * @param map the map to check
   * @return true if the map is not null and contains key-value pairs
   */
  public static boolean isNotNullOrEmpty(Map<?, ?> map) {
    return !isNullOrEmpty(map);
  }

  /**
   * Checks if an array is null or empty.
   *
   * @param array the array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(Object[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if an array is not null and not empty.
   *
   * @param array the array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(Object[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive byte array is null or empty.
   *
   * @param array the byte array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(byte[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive byte array is not null and not empty.
   *
   * @param array the byte array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(byte[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive int array is null or empty.
   *
   * @param array the int array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(int[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive int array is not null and not empty.
   *
   * @param array the int array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(int[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive long array is null or empty.
   *
   * @param array the long array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(long[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive long array is not null and not empty.
   *
   * @param array the long array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(long[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive double array is null or empty.
   *
   * @param array the double array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(double[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive double array is not null and not empty.
   *
   * @param array the double array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(double[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive float array is null or empty.
   *
   * @param array the float array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(float[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive float array is not null and not empty.
   *
   * @param array the float array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(float[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive boolean array is null or empty.
   *
   * @param array the boolean array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(boolean[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive boolean array is not null and not empty.
   *
   * @param array the boolean array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(boolean[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive char array is null or empty.
   *
   * @param array the char array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(char[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive char array is not null and not empty.
   *
   * @param array the char array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(char[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if a primitive short array is null or empty.
   *
   * @param array the short array to check
   * @return true if the array is null or has zero length
   */
  public static boolean isNullOrEmpty(short[] array) {
    return array == null || array.length == 0;
  }

  /**
   * Checks if a primitive short array is not null and not empty.
   *
   * @param array the short array to check
   * @return true if the array is not null and has elements
   */
  public static boolean isNotNullOrEmpty(short[] array) {
    return !isNullOrEmpty(array);
  }

  /**
   * Checks if any of the provided objects are null.
   *
   * @param objects the objects to check
   * @return true if any of the objects is null
   */
  public static boolean isAnyNull(Object... objects) {
    if (objects == null) {
      return true;
    }
    for (Object obj : objects) {
      if (obj == null) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks if all of the provided objects are null.
   *
   * @param objects the objects to check
   * @return true if all objects are null
   */
  public static boolean isAllNull(Object... objects) {
    if (objects == null) {
      return true;
    }
    for (Object obj : objects) {
      if (obj != null) {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks if none of the provided objects are null.
   *
   * @param objects the objects to check
   * @return true if none of the objects is null
   */
  public static boolean isNoneNull(Object... objects) {
    return !isAnyNull(objects);
  }

  /**
   * Recursively searches for a value that starts with the given prefix in a map or object
   * structure. Searches through nested maps, lists, and arrays.
   *
   * @param obj the object to search in (can be Map, List, Array, or other types)
   * @param prefix the prefix to search for
   * @return the first value found that starts with the prefix, or null if not found
   */
  public static String findValueByPrefix(Object obj, String prefix) {
    if (obj == null || prefix == null) {
      return null;
    }

    // Check if it's a String that starts with the prefix
    if (obj instanceof String str) {
      if (str.startsWith(prefix)) {
        return str;
      }
      return null;
    }

    // Check if it's a Map
    if (obj instanceof Map<?, ?> map) {
      for (Object value : map.values()) {
        String result = findValueByPrefix(value, prefix);
        if (result != null) {
          return result;
        }
      }
    }

    // Check if it's a List
    if (obj instanceof java.util.List<?> list) {
      for (Object item : list) {
        String result = findValueByPrefix(item, prefix);
        if (result != null) {
          return result;
        }
      }
    }

    // Check if it's an Array
    if (obj.getClass().isArray()) {
      int length = java.lang.reflect.Array.getLength(obj);
      for (int i = 0; i < length; i++) {
        Object item = java.lang.reflect.Array.get(obj, i);
        String result = findValueByPrefix(item, prefix);
        if (result != null) {
          return result;
        }
      }
    }

    return null;
  }
}

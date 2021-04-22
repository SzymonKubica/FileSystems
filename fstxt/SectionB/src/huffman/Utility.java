package huffman;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Utility {

  public static List<String> getWords(String filePath) {
    List<String> words = null;
    try (Stream<String> linesStream = Files.lines(Paths.get(filePath))) {
      words = linesStream.flatMap(line -> Arrays.stream(line.split(" "))).map(String::trim)
              .collect(Collectors.toList());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return words;
  }

  public static String sequenceOfBitsAsNumber(String binaryEncoding) {
    final String binaryEncodingWithHeading1 =
            "1" + binaryEncoding; // Prepending 1 not to lose heading zeroes
    BigInteger result = new BigInteger(binaryEncodingWithHeading1, 2);
    return result.toString();
  }

  public static String numberAsSequenceOfBits(String numberRepresentation) {
    BigInteger number = new BigInteger(numberRepresentation);
    String binaryRepresentation = number.toString(2);
    return binaryRepresentation.substring(1); // Removing previously prepended 1
  }

  public static long totalLength(List<String> words) {
    long length = words.size() - 1; // White spaces
    length += words.stream().mapToLong(String::length).sum();
    return length;
  }

  public static Map<String, Integer> countWords(List<String> words) {
    //TODO replace the current sequenctial implementation with a concurrent one (Q4)
    //return words.stream().collect(Collectors.toMap(w -> w, w -> 1, Integer::sum));
    List<String> firstHalf = words.subList(0, (words.size() - 1) / 2);
    List<String> secondHalf = words.subList((words.size() - 1) / 2, words.size());

    WordCounter counter1 = new WordCounter(firstHalf, new HashMap<>());
    WordCounter counter2 = new WordCounter(secondHalf, new HashMap<>());

    Thread thread1 = new Thread(counter1);
    Thread thread2 = new Thread(counter2);

    thread1.start();
    thread2.start();

    try {
      thread1.join();
      thread2.join();
    } catch (InterruptedException e) {
      System.out.println(e.getMessage());
    }

    Map<String, Integer> firstHalfMap = counter1.destinationMap;
    Map<String, Integer> secondHalfMap = counter2.destinationMap;
    for (Map.Entry<String, Integer> entry : firstHalfMap.entrySet()) {
      if (secondHalfMap.containsKey(entry.getKey())) {
        secondHalfMap.put(entry.getKey(), entry.getValue() + secondHalfMap.get(entry.getKey()));
      } else {
        secondHalfMap.put(entry.getKey(), entry.getValue());
      }
    }
    return secondHalfMap;
  }

  private static class WordCounter implements Runnable {
    private List<String> listToCount;
    private Map<String, Integer> destinationMap;

    public WordCounter(List<String> listToCount, Map<String, Integer> destinationMap) {
      this.listToCount = listToCount;
      this.destinationMap = destinationMap;
    }

    @Override
    public void run() {
      destinationMap = listToCount.stream().collect(Collectors.toMap(w -> w, w -> 1, Integer::sum));
    }
  }
}

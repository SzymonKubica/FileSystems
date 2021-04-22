package huffman;


import java.util.*;

public class HuffmanEncoder {

  final HuffmanNode root;
  final Map<String, String> word2bitsequence;

  private HuffmanEncoder(HuffmanNode root,
                         Map<String, String> word2bitSequence) {
    this.root = root;
    this.word2bitsequence = word2bitSequence;
  }

  public static HuffmanEncoder buildEncoder(Map<String, Integer> wordCounts) {

    if (wordCounts == null) {
      throw new HuffmanEncoderException("wordCounts cannot be null");
    }
    if (wordCounts.size() < 2) {
      throw new HuffmanEncoderException("This encoder requires at least two different words");
    }

    // fixing the order in which words will be processed: this determinize the execution and makes
    // tests reproducible.
    TreeMap<String, Integer> sortedWords = new TreeMap<>(wordCounts);
    PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(sortedWords.size());

    for (Map.Entry<String, Integer> wordAndItsCount : sortedWords.entrySet()) {
      queue.add(new HuffmanLeaf(wordAndItsCount.getValue(), wordAndItsCount.getKey()));
    }

    while (queue.size() >= 2) {
      HuffmanNode left = queue.poll();
      HuffmanNode right = queue.poll();
      HuffmanNode newInternalNode = new HuffmanInternalNode(left, right);
      queue.offer(newInternalNode);
    }

    //YOUR IMPLEMENTATION HERE...
    HuffmanNode root = queue.poll();
    Map<String, String> word2bitSequence = produceWord2bitSequence(root);

    return new HuffmanEncoder(root, word2bitSequence);
  }

  private static Map<String, String> produceWord2bitSequence(HuffmanNode root) {
    Map<String, String> word2bitSequence = new HashMap<>();
    populateWord2bitSequenceMap(root, "", word2bitSequence);
    return word2bitSequence;

  }

  private static void populateWord2bitSequenceMap(
          HuffmanNode root,
          String pendingBitSequence,
          Map<String, String> word2bitSequence
  ) {
    if (root instanceof HuffmanLeaf) {
      word2bitSequence.put(((HuffmanLeaf) root).word, pendingBitSequence);
    } else {
      populateWord2bitSequenceMap(
              ((HuffmanInternalNode) root).left,
              pendingBitSequence + "0",
              word2bitSequence
      );
      populateWord2bitSequenceMap(
              ((HuffmanInternalNode) root).right,
              pendingBitSequence + "1",
              word2bitSequence
      );
    }
  }


  public String compress(List<String> text) {
    assert text != null && text.size() > 0;
    if (containsUnknownWords(text)) {
      throw new HuffmanEncoderException();
    }
    return text.stream().map(word2bitsequence::get).reduce(String::concat).get();
  }

  private boolean containsUnknownWords(List<String> text) {
    for (String word : text) {
      if (!word2bitsequence.containsKey(word)) {
        return true;
      }
    }
    return false;
  }


  public List<String> decompress(String compressedText) {
    assert compressedText != null && compressedText.length() > 0;
    List<String> decompressedText = new ArrayList<>();
    while (compressedText.length() > 0) {
      HuffmanNode currentNode = root;
      while (currentNode instanceof HuffmanInternalNode) {
        if (compressedText.charAt(0) == '0') {
          currentNode = ((HuffmanInternalNode) currentNode).left;
        } else {
          currentNode = ((HuffmanInternalNode) currentNode).right;
        }
        compressedText = compressedText.substring(1);
        if (compressedText.length() == 0 && !(currentNode instanceof HuffmanLeaf)) {
          throw new HuffmanEncoderException();
        }
      }
      decompressedText.add(((HuffmanLeaf) currentNode).word);
    }
    return decompressedText;
  }

  // Below the classes representing the tree's nodes. There should be no need to modify them, but
  // feel free to do it if you see it fit

  private abstract static class HuffmanNode implements Comparable<HuffmanNode> {

    private final int count;

    public HuffmanNode(int count) {
      this.count = count;
    }

    @Override
    public int compareTo(HuffmanNode otherNode) {
      return count - otherNode.count;
    }
  }


  private static class HuffmanLeaf extends HuffmanNode {

    private final String word;

    public HuffmanLeaf(int frequency, String word) {
      super(frequency);
      this.word = word;
    }
  }


  private static class HuffmanInternalNode extends HuffmanNode {

    private final HuffmanNode left;
    private final HuffmanNode right;

    public HuffmanInternalNode(HuffmanNode left, HuffmanNode right) {
      super(left.count + right.count);
      this.left = left;
      this.right = right;
    }
  }
}

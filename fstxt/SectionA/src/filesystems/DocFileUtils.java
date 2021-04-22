package filesystems;

import java.util.Optional;

public class DocFileUtils {

  /**
   * Compute the total size, in bytes, of the directory and all of its contents.
   * @param directory A directory.
   * @return The size of this directory plus, the sum of the sizes of all files contained directly
   *         or indirectly in this directory.
   */
  public static int getTotalDirectorySize(DocDirectory directory) {
    int totalSize = 0;
    for (DocFile file : directory.getAllFiles()) {
      if (file.isDataFile()) {
        totalSize += file.getSize();
      } else {
        totalSize += getTotalDirectorySize(file.asDirectory());
      }
    }
    return totalSize + directory.getSize();
  }

  /**
   * Copy a named file between directories.
   * @param src A source directory.
   * @param dst A destination directory.
   * @param filename The name of a file to be copied.
   * @return False if the source directory does not contain a file with the given name, or
   *         if the destination directory already contains a file with the given name.  Otherwise,
   *         create a duplicate of the file with the given name in the source directory and add
   *         this duplicate to the destination directory.
   */
  public static boolean copy(DocDirectory src, DocDirectory dst, String filename) {
    if (src.containsFile(filename)) {
      dst.addFile(src.getFile(filename).duplicate());
      return true;
    } else {
      return false;
    }
  }

  /**
   * Locate a file containing a given byte and lying at or beneath a given file, if one exists.
   * @param root A file, to be recursively searched.
   * @param someByte A byte to be searched for.
   * @return An empty optional if no file at or beneath the given root file contains the
   *         given byte.  Otherwise, return an optional containing any such file.
   */
  public static Optional<DocDataFile> searchForByte(DocFile root, byte someByte) {
    if (root.isDataFile()) {
      return searchForByteInDataFile(root.asDataFile(), someByte);
    } else {
      for (DocFile file : root.asDirectory().getAllFiles()) {
        if (searchForByte(file, someByte).isPresent()) {
          return searchForByte(file, someByte);
        }
      }
      return Optional.empty();
    }
  }

  private static Optional<DocDataFile> searchForByteInDataFile(DocDataFile file, byte someByte) {
    if (file.containsByte(someByte)) {
      return Optional.of(file);
    } else {
      return Optional.empty();
    }
  }

}

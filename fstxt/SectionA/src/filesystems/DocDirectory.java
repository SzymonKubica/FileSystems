package filesystems;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DocDirectory extends DocFile {
  private Set<DocFile> files;

  public DocDirectory(String name) {
    super(name);
    this.files = new HashSet<>();
  }

  public boolean containsFile(String name) {
    return !files.stream().filter(file -> file.getName().equals(name))
            .collect(Collectors.toSet()).isEmpty();
  }

  public Set<DocFile> getAllFiles() {
    return files;
  }

  public Set<DocDirectory> getDirectories() {
    return files.stream().filter(file -> file instanceof DocDirectory)
            .map(DocFile::asDirectory).collect(Collectors.toSet());
  }

  public Set<DocDataFile> getDataFiles() {
    return files.stream().filter(file -> file instanceof DocDataFile)
            .map(DocFile::asDataFile).collect(Collectors.toSet());
  }

  public void addFile(DocFile file) {
    if (containsFile(file.getName())) {
      throw new IllegalArgumentException();
    } else {
      files.add(file);
    }
  }

  public boolean removeFile(String filename) {
    if (containsFile(filename)) {
      return files.remove(getFile(filename));
    } else {
      return false;
    }
  }

  public DocFile getFile(String filename) {
    assert containsFile(filename);
    return files.stream().filter(file -> file.getName().equals(filename))
            .collect(Collectors.toList()).get(0);
  }

  @Override
  public int getSize() {
    return getName().length();
  }

  @Override
  public boolean isDirectory() {
    return true;
  }

  @Override
  public boolean isDataFile() {
    return false;
  }

  @Override
  public DocDirectory asDirectory() {
    return this;
  }

  @Override
  public DocDataFile asDataFile() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DocFile duplicate() {
    DocDirectory directory = new DocDirectory(getName());
    for (DocFile file : files) {
      directory.addFile(file.duplicate());
    }
    return directory;
  }
}

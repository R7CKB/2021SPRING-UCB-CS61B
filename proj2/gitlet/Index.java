package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

public class Index implements Serializable, Dumpable {
    /**
     * The file where the index is stored.
     */
    static final File indexFile = new File(join(Repository.GITLET_DIR), "index");

    /**
     * The entries of the index, mapping filenames to their corresponding blob ids.
     */
    private final Map<String, String> addEntries;

    /**
     * The entries of the index, mapping filenames to their corresponding blob ids.
     */
    private final Map<String, String> removeEntries;

    public Index() {
        this(new TreeMap<>(), new TreeMap<>());
    }

    public Index(Map<String, String> addEntries, Map<String, String> removeEntries) {
        this.addEntries = addEntries;
        this.removeEntries = removeEntries;
        saveFile();
    }

    public void addAdd(Blob blob) {
        addEntries.put(blob.getFilename(), blob.getId());
    }

    public void removeAdd(Blob blob) {
        removeEntries.put(blob.getFilename(), blob.getId());
    }

    public void addRemove(String filename) {
        addEntries.remove(filename);
    }

    public void removeRemove(String filename) {
        removeEntries.remove(filename);
    }

    public boolean addContainsFile(String filename) {
        if (addEntries.isEmpty()) return false;
        return addEntries.containsKey(filename);
    }

    public boolean addContainsId(String id) {
        if (addEntries.isEmpty()) return false;
        return addEntries.containsValue(id);
    }

    public boolean removeContainsFile(String filename) {
        if (removeEntries.isEmpty()) return false;
        return removeEntries.containsKey(filename);
    }

    public boolean removeContainsId(String id) {
        if (removeEntries.isEmpty()) return false;
        return removeEntries.containsValue(id);
    }

    public Index fromFile(String filename) {
        Index index = null;
        File indexFile = new File(Repository.INDEX_FILE, filename);
        index = readObject(indexFile, Index.class);
        return index;
    }

    public void clearFile() {
        addEntries.clear();
        removeEntries.clear();
        saveFile();
    }

    public Map<String, String> getAddBlobs() {
        return addEntries;
    }

    public Map<String, String> getRemoveBlobs() {
        return removeEntries;
    }

    public boolean addIsEmpty() {
        return addEntries.isEmpty();
    }

    public boolean removeIsEmpty() {
        return removeEntries.isEmpty();
    }

    public void saveFile() {
        File indexFile = Repository.INDEX_FILE;
        writeObject(indexFile, this);
    }

    @Override
    public void dump() {
        System.out.println("Index:");
        System.out.println("Add Entries:");
        if (!addEntries.isEmpty()) {
            for (String filename : addEntries.keySet()) {
                System.out.println(filename + " -> " + addEntries.get(filename));
            }
        }
        System.out.println("Remove Entries:");
        if (!removeEntries.isEmpty()) {
            for (String filename : removeEntries.keySet()) {
                System.out.println(filename + " -> " + removeEntries.get(filename));
            }
        }
    }
}

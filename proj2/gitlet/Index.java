package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/**
 * Represents the index(stage area) of a repository.
 * The index is a collection of blobs that'd been added or removed from the working directory,
 * but not yet committed.
 * The index is stored in a file named "index" in the repository's root directory.
 * The index is a map of filenames to their corresponding blob ids.
 * The index is also responsible for managing the add and remove entries of the index.
 *
 * @author R7CKB
 */
public class Index implements Serializable {

    /**
     * The file where the index is stored.
     */
    static final File INDEX_FILE = new File(join(Repository.GITLET_DIR), "index");

    /**
     * The entries of the index, mapping filenames to their corresponding blob ids.
     */
    private final Map<String, String> addEntries;

    /**
     * The entries of the index, mapping filenames to their corresponding blob ids.
     */
    private final Map<String, String> removeEntries;

    /**
     * Creates a new empty index.
     * The index is initially empty.
     */
    public Index() {
        this(new TreeMap<>(), new TreeMap<>());
    }

    /**
     * Creates a new index with the given add and remove entries.
     *
     * @param addEntries    the adding entries of the index,
     *                      mapping filenames to their corresponding blob ids.
     * @param removeEntries the remove entries of the index,
     *                      mapping filenames to their corresponding blob ids.
     */
    public Index(Map<String, String> addEntries, Map<String, String> removeEntries) {
        this.addEntries = addEntries;
        this.removeEntries = removeEntries;
        saveFile();
    }

    /**
     * Adds a blob to the added entry of the index.
     *
     * @param blob the blob to be added.
     */
    public void addAdd(Blob blob) {
        addEntries.put(blob.getFilename(), blob.getId());
    }

    /**
     * Adds a blob to the removed entry of the index.
     *
     * @param blob the blob to be removed.
     */
    public void removeAdd(Blob blob) {
        removeEntries.put(blob.getFilename(), blob.getId());
    }

    /**
     * Removes a blob from the removed entry of the index.
     *
     * @param filename the filename of the blob to be removed.
     */
    public void addRemove(String filename) {
        addEntries.remove(filename);
    }

    /**
     * Removes a blob from the added entry of the index.
     *
     * @param filename the filename of the blob to be removed.
     */
    public void removeRemove(String filename) {
        removeEntries.remove(filename);
    }

    /**
     * Returns true if the added entry of the index with the given filename.
     * Returns false otherwise.
     *
     * @param filename the filename of the added entry(AKA Key).
     * @return the boolean value indicating whether
     * the added entry of the index contains the given filename.
     */
    public boolean addContainsFile(String filename) {
        if (addEntries.isEmpty()) {
            return false;
        }
        return addEntries.containsKey(filename);
    }

    /**
     * Returns true if the added entry of the index with the given id.
     * Returns false otherwise.
     *
     * @param id the value of the id of the removed entry(AKA Value).
     * @return the boolean value indicating whether the added entry of the index with the given id.
     */
    public boolean addContainsId(String id) {
        if (addEntries.isEmpty()) {
            return false;
        }
        return addEntries.containsValue(id);
    }

    /**
     * Returns true if the removed entry of the index with the given filename.
     * Returns false otherwise.
     *
     * @param filename the filename of the removed entry(AKA Key).
     * @return the boolean value indicating whether
     * the removed entry of the index contains the given filename.
     */
    public boolean removeContainsFile(String filename) {
        if (removeEntries.isEmpty()) {
            return false;
        }
        return removeEntries.containsKey(filename);
    }

    /**
     * Returns true if the removed entry of the index with the given id.
     * Returns false otherwise.
     *
     * @param id the value of the id of the removed entry(AKA Value).
     * @return the boolean value indicating whether
     * the removed entry of the index contains the given id.
     */
    public boolean removeContainsId(String id) {
        if (removeEntries.isEmpty()) {
            return false;
        }
        return removeEntries.containsValue(id);
    }

    /**
     * clear the index file.
     */
    public void clearFile() {
        addEntries.clear();
        removeEntries.clear();
        saveFile();
    }

    /**
     * Returns the added entry of the index.
     *
     * @return the added entry of the index.
     */
    public Map<String, String> getAddBlobs() {
        return addEntries;
    }

    /**
     * Returns the removed entry of the index.
     *
     * @return the removed entry of the index.
     */
    public Map<String, String> getRemoveBlobs() {
        return removeEntries;
    }

    /**
     * Returns true if the added entry of the index is empty.
     *
     * @return the boolean value indicating whether the added entry of the index is empty.
     */
    public boolean addIsEmpty() {
        return addEntries.isEmpty();
    }

    /**
     * Returns true if the removed entry of the index is empty.
     *
     * @return the boolean value indicating whether the removed entry of the index is empty.
     */
    public boolean removeIsEmpty() {
        return removeEntries.isEmpty();
    }

    /**
     * Saves the index to the index file.
     */
    public void saveFile() {
        writeObject(INDEX_FILE, this);
    }

    //    /**
    //     * Prints the index to the console.
    //     * only for debug
    //     */
    //    @Override
    //    public void dump() {
    //        System.out.println("Index:");
    //        System.out.println("Add Entries:");
    //        if (!addEntries.isEmpty()) {
    //            for (String filename : addEntries.keySet()) {
    //                System.out.println(filename + " -> " + addEntries.get(filename));
    //            }
    //        }
    //        System.out.println("Remove Entries:");
    //        if (!removeEntries.isEmpty()) {
    //            for (String filename : removeEntries.keySet()) {
    //                System.out.println(filename + " -> " + removeEntries.get(filename));
    //            }
    //        }
    //    }
}

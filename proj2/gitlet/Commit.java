package gitlet;


import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;


/**
 * Represents a gitlet commit object.
 * A Commit object contains the following information *: the message of the commit.
 * - timeStamp: the date and time of the commit.
 * - id: the SHA-1 hash of the commit.
 * - parents: the parent Commits of the commit.
 * - blobMap: the blobMap of the commit.
 * The Commit object is responsible for serializing and deserializing itself to and from a file.
 * It also provides methods for getting and setting its fields,
 * as well as for saving and loading it to and from a file.
 * The blobMap is a map that maps the filename to the SHA-1 hash of the corresponding blob.
 *
 * @author R7CKB
 */
public class Commit implements Serializable, Dumpable {

    /**
     * The commits' directory.
     * This directory addContainsId all the commits of the repository.
     */
    static final File COMMITS_DIR = join(Repository.OBJECTS_DIR, "commits");

    /**
     * The message of this Commit.
     */
    private final String message;

    /**
     * The dateString of this Commit.
     */
    private String timeStamp;

    /**
     * The commit ID of this Commit(SHA-1 hash).
     */
    private final String id;

    /**
     * The parent Commits of this Commit.
     * In the gitlet, we only support two parents commit.
     * The first parent is the parent of the current branch,
     * and the second parent is the parent of the other branch.
     */
    private final List<String> parents;


    /**
     * The blobMap of this Commit.
     */
    private final Map<String, String> blobMap;

    /**
     * Constructor for an empty Commit object.
     */
    public Commit() {
        this("initial commit", new Date(0), new ArrayList<>(), new TreeMap<>());
    }

    /**
     * Constructor for a Commit object.
     *
     * @param message the message of this Commit.
     * @param date    the date of this Commit.
     * @param parents the parent Commits of this Commit.
     * @param blobMap the blobMap of this Commit.
     */
    public Commit(String message, Date date, List<String> parents, Map<String, String> blobMap) {
        this.message = message;
        this.timeStamp = handleDate(date);
        this.parents = parents;
        this.blobMap = blobMap;
        this.id = sha1(message, timeStamp, parents.toString(), blobMap.toString());
        saveCommit();
    }

    /**
     * get a message of this Commit
     *
     * @return the message of this Commit
     */
    public String getMessage() {
        return message;
    }

    /**
     * get dateString of this Commit
     *
     * @return the dateString of this Commit
     */
    public String getTimeStamp() {
        return timeStamp;
    }

    /**
     * Returns true if the commit contains the given id.
     *
     * @return true if the commit contains the given id, false otherwise.
     */
    public boolean containsId(String fileId) {
        if (blobMap.isEmpty()) {
            return false;
        }
        return blobMap.containsValue(fileId);
    }

    /**
     * Returns true if the commit contains the given file.
     *
     * @return true if the commit contains the given file, false otherwise.
     */
    public boolean containsFile(String filename) {
        if (blobMap.isEmpty()) {
            return false;
        }
        return blobMap.containsKey(filename);
    }

    /**
     * Returns the commit ID of this Commit(SHA-1 hash).
     *
     * @return the commit ID of this Commit(SHA-1 hash).
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the parent Commits of this Commit.
     *
     * @return the parent Commits of this Commit.
     */
    public List<String> getParents() {
        return parents;
    }

    /**
     * Returns the blobMap of this Commit.
     *
     * @return the blobMap of this Commit.
     */
    public Map<String, String> getBlobMap() {
        return blobMap;
    }

    /**
     * output the log of the commit.
     */
    @Override
    public void dump() {
        List<String> listOfParents = getParents();
        System.out.println("===");
        System.out.println("commit " + this.getId());
        if (listOfParents.size() == 2) {
            Commit parent1 = fromFile(listOfParents.get(0));
            Commit parent2 = fromFile(listOfParents.get(1));
            System.out.println("Merge: " + parent1.getId().substring(0, 7)
                    + " " + parent2.getId().substring(0, 7));
        }
        System.out.println("Date: " + this.getTimeStamp());
        System.out.println(this.getMessage());
        System.out.println();
        // the following code is for debugging purposes only.
        // System.out.println("Parents: " + this.getParents());
        // System.out.println("BlobMap: " + this.getBlobMap());
    }


    /**
     * convert date to string format
     *
     * @param date the date to be converted
     * @return the string format of the date
     */
    private String handleDate(Date date) {
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat ft = new SimpleDateFormat("E MMM dd HH:mm:ss yyyy Z", Locale.US);
        // PST is the time zone of the Pacific Standard Time.
        ft.setTimeZone(TimeZone.getTimeZone("PST"));
        // The format of the date string is "Wed Dec 31 16:00:00 1969 -0800"
        this.timeStamp = ft.format(date);
        return timeStamp;
    }

    /**
     * Reads in and deserializes a Commit object from a file with filename in OBJECTS_DIR.
     *
     * @param filename name of file to read from
     * @return The Commit object
     */
    public static Commit fromFile(String filename) {
        Commit commit = null;
        File commitFile = new File(COMMITS_DIR, filename);
        commit = readObject(commitFile, Commit.class);
        return commit;
    }

    /**
     * Saves this Commit object to a file for future use.
     */
    public void saveCommit() {
        File commitFile = new File(COMMITS_DIR, getId());
        writeObject(commitFile, this);
    }
}

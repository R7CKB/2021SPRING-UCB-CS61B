package gitlet;

import java.io.File;
import java.io.Serializable;

import static gitlet.Utils.*;

/**
 * Represents a blob object in Gitlet.
 * It contains a filename and content, and computes its id as the name of the blob file.
 *
 * @author R7CKB
 */
public class Blob implements Serializable {

    /**
     * The blobs' directory.
     * This directory addContainsId all the trees of the repository.
     */
    static final File BLOBS_DIR = join(Repository.OBJECTS_DIR, "blobs");

    /**
     * This filename is the file original name.
     */
    private final String filename;

    /**
     * This content is the actual data of the file.
     */
    private final String content;

    /**
     * The blob's id.
     * This id is the SHA-1 hash of the filename and content.
     */
    private final String id;


    /**
     * Create a new blob with the given filename, id, and content.
     * Every time you create a blob,
     * the corresponding blob file is automatically generated in the blobs' directory.
     *
     * @param filename the filename of the blob.
     * @param content  the content of the blob.
     */
    public Blob(String filename, String content) {
        this.filename = filename;
        this.id = sha1(filename, content);
        this.content = content;
        saveBlob();
    }

    /**
     * Get the filename of the blob.
     *
     * @return the filename of the blob.
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Get the id of the blob.
     *
     * @return the id of the blob.
     */
    public String getId() {
        return id;
    }

    /**
     * Get the content of the blob.
     *
     * @return the content of the blob.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the blob object from the given id.
     *
     * @param id the id of the blob.
     * @return the blob object.
     */
    public static Blob fromFile(String id) {
        Blob blob = null;
        File blobFile = new File(BLOBS_DIR, id);
        blob = readObject(blobFile, Blob.class);
        return blob;

    }

    /**
     * Save the blob object to the corresponding blob file.
     */
    public void saveBlob() {
        File blobFile = new File(BLOBS_DIR, id);
        writeObject(blobFile, this);
    }

    // This is only for debugging
    //    @Override
    //    public void dump() {
    //        Blob blob = fromFile(filename);
    //        System.out.println(blob);
    //    }
}

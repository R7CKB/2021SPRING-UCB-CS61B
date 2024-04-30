package gitlet;

// reference:https://zhuanlan.zhihu.com/p/533852291
// not refer its code, but it's a good reference.

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;


import static gitlet.Utils.*;

// TODO: any imports you need here

/**
 * Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 * @author R7CKB
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    // the default File structure like the following:
    //.gitlet/
    // ├── HEAD (store the current branch name)
    // ├── index (the index for staging area)
    // ├── objects/
    // │   ├── blobs/
    // │   └── commits/
    // └── refs/
    //     └── heads/
    //         ├──  master (store the current commit id of the master branch)
    //         └──  other branch (...)
    // the .gitlet directory contains all the files and directories of the repository.
    // the objects' directory contains all the objects (commits, blobs) of the repository.
    // the refs' directory contains all the references (heads, remotes) of the repository.
    // the HEAD file saves a reference to the current branch.
    // the index file for staging area.
    // the current branch(default is master).

    /**
     * The current working directory.
     * This is the directory where the user is currently working.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));

    /**
     * The .gitlet directory.
     * This directory addContainsId all the files and directories of the repository.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * The objects' directory.
     * This directory addContainsId all the objects (commits, trees, blobs) of the repository.
     */
    public static final File OBJECTS_DIR = join(GITLET_DIR, "objects");

    /**
     * The refs' directory.
     * This directory addContainsId all the references (heads, tags, remotes) of the repository.
     */
    public static final File BRANCH_DIR = join(GITLET_DIR, "refs", "heads");

    /**
     * The HEAD file.
     * Save a reference to the current branch.
     * Usually a symbolic link pointing to a branch under refs/heads/.
     */
    public static final File HEAD_FILE = new File(join(GITLET_DIR), "HEAD");

    /**
     * The index file for staging area.
     */
    public static final File INDEX_FILE = new File(join(GITLET_DIR), "index");

    /**
     * The current branch(default is master).
     */
    public static final File BRANCH_FILE = new File(join(BRANCH_DIR), "master");

    /* TODO: fill in the rest of this class. */

    /**
     * As a helper method to help initialize the repository.
     */
    public static void setUpPersistence() {
        boolean gitletDirExists = GITLET_DIR.mkdir(); // create.gitlet directory if it doesn't exist
        boolean indexFileExists = Index.indexFile.exists(); // check if the index file exists
        boolean headFileExists = HEAD_FILE.exists(); // check if HEAD file exists

        boolean objectsDirExists = OBJECTS_DIR.mkdir(); // create objects directory if it doesn't exist
        boolean commitsDirExist = Commit.COMMITS_DIR.mkdir(); // create commits directory if it doesn't exist
        boolean blobsDirExist = Blob.BLOBS_DIR.mkdir(); // create blobs directory if it doesn't exist

        boolean branchDirExists = BRANCH_DIR.mkdirs(); // create refs/heads/ directory if it doesn't exist
        boolean masterExists = BRANCH_FILE.exists(); // check if the master branch file exists
        if (!gitletDirExists) {
            Utils.message("A Gitlet version-control system already " +
                    "exists in the current directory.");
            System.exit(0);
        }
        if (!indexFileExists && !headFileExists && !masterExists && branchDirExists
                && objectsDirExists && commitsDirExist && blobsDirExist) {
            try {
                boolean headExists = HEAD_FILE.createNewFile(); // create HEAD file if it doesn't exist
                boolean indexExists = INDEX_FILE.createNewFile(); // create index file if it doesn't exist
                boolean materExists = BRANCH_FILE.createNewFile(); // create master branch file
                // if it doesn't exist
                // creates an initial commit, and initialize the index,head, and master branch.
                Commit initalCommit = new Commit();
                Index index = new Index();
                writeContents(HEAD_FILE, "master");
                writeContents(BRANCH_FILE, initalCommit.getId());
            } catch (Exception e) {
                System.err.println("Error in initializing repository: " + e.getMessage());
            }
        }
    }


    /**
     * Initializes a new repository in the current directory.
     */
    public static void init() {
        // mimic lab6 setupPersistence() method
        setUpPersistence();
    }


    /**
     * Adds a file to the repository's index.
     *
     * @param filename the name of the file to add into the index.
     */
    public static void add(String filename) {
        String branch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        Index index = readObject(INDEX_FILE, Index.class);
        File file = new File(join(CWD), filename);
        //  If the file doesn't exist (neither in the working directory nor in the index)
        if (!file.exists()) {
            Utils.message("File does not exist.");
            System.exit(0);
        }
        // create a blob object for the file and serialize it to disk.
        String content = readContentsAsString(file);
        Blob blob = new Blob(filename, content);
        addJudge(index, blob, filename, blob.getId(), currentCommit);
        index.saveFile();
    }


    /**
     * as a helper method to add a blob to the index.
     *
     * @param index    the index to add the blob to.
     * @param blob     the blob to add to the index.
     * @param filename the name of the file.
     * @param id       the id of the file.
     * @param commit   the current commit of the current head.
     */
    private static void addJudge(Index index, Blob blob, String filename, String id, Commit commit) {
        // add the blob to the index, including overwriting existing blob.
        index.addAdd(blob);
        //  If the current working version of the file is identical to the version in the current commit,
        //  don't stage it to be added, and remove it from the staging area if it's already there
        if (commit.containsId(id)) {
            // don't add the file to index if it's as same as the current commit.
            index.addRemove(filename);
        }
        if (index.removeContainsId(id)) {
            // The file will no longer be staged for removal (see gitlet rm)
            // if it was at the time of the command.
            index.removeRemove(filename);
        }
    }


    /**
     * make a commit of the current index with the given message on the current branch.
     *
     * @param message the message of the commit.
     */
    public static void commit(String message) {
        String branch = readContentsAsString(HEAD_FILE);
        File branchFile = new File(BRANCH_DIR, branch);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(branchFile)), Commit.class);
        Index index = readObject(INDEX_FILE, Index.class);
        // Failure case: no files have been staged TODO:including add and removed files? YES
        if (index.addIsEmpty() && index.removeIsEmpty()) {
            Utils.message("No changes added to the commit.");
            System.exit(0);
        }
        ArrayList<String> parents = new ArrayList<>();
        parents.add(currentCommit.getId());
        Map<String, String> finalBlobs = dealWithStageAndCommitBlobs(index, currentCommit);
        // create a new commit object with the current index and message.
        Commit newCommit = new Commit(message, new Date(), parents, finalBlobs);
        // Update the master branch to point to the new commit.
        writeContents(branchFile, newCommit.getId());
        // update the index to be empty.
        index.clearFile();
    }

    private static Map<String, String> handleRemoveBlobs(Map<String, String> stageBlobs,
                                                         Map<String, String> commitBlobs) {
        for (String filename : stageBlobs.keySet()) {
            commitBlobs.remove(filename);
        }
        return commitBlobs;
    }

    /**
     * As a helper method to handle the blobs of the current commit and the new commit.
     *
     * @param stageBlobs  the blobs in the index.
     * @param commitBlobs the blobs in the current commit.
     * @return the new blobs of the new commit.
     */
    private static Map<String, String> handleBlobs(Map<String, String> stageBlobs,
                                                   Map<String, String> commitBlobs) {
        // if the current commit is initial commit, return the current blobs(index files).
        if (commitBlobs.isEmpty()) {
            return stageBlobs;
        } else {
            for (Map.Entry<String, String> entry : stageBlobs.entrySet()) {
                String filename = entry.getKey();
                String id = entry.getValue();
                if (!commitBlobs.containsKey(filename) || !commitBlobs.containsValue(id)) {
                    // if the blob is already in the current commit,
                    // don't add it to the new commit.
                    commitBlobs.put(filename, id);
                }
            }
        }
        return commitBlobs;
    }

    /**
     * remove the file from the working directory and the index.
     *
     * @param filename the name of the file to remove.
     */
    public static void rm(String filename) {
        String branch = readContentsAsString(HEAD_FILE);
        Index index = readObject(INDEX_FILE, Index.class);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        File removedFile = new File(join(CWD), filename);
        String id = currentCommit.getBlobMap().get(filename);
        // The head commit neither stages nor tracks the file
        if (!index.addContainsFile(filename) && !index.removeContainsFile(filename) &&
                !currentCommit.containsFile(filename)) {
            Utils.message("No reason to remove the file.");
            System.exit(0);
        } else if (index.addContainsFile(filename)) {
            // Unstage the file if it is currently staged for addition
            index.addRemove(filename);
        } else if (currentCommit.containsFile(filename) && removedFile.exists()) {
            // If the file is tracked in the current commit, stage it for removal
            // and remove the file from the working directory if the user hasn't already done so
            Blob blob = Blob.fromFile(id);
            index.removeAdd(blob);
            restrictedDelete(removedFile);
        } else {
            // the user deleted the file before rm command, so we don't need to delete it.
            Blob blob = Blob.fromFile(id);
            index.removeAdd(blob);
        }
        // update the index file.
        index.saveFile();
    }


    /**
     * Prints the logs of the current branch.
     * The concrete format is implemented in the Commit.dump() method.
     */
    public static void log() {
        // TODO: check if have conflicts with merge?
        String branch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        List<String> listOfParents = currentCommit.getParents();
        // In the dump method, we handle the case that the current commit has two parents.
        // All we need to do is make sure the first parent is the parent of the current branch.
        while (!listOfParents.isEmpty()) {
            currentCommit.dump();
            String parentId = listOfParents.get(0);
            Commit parentCommit = readObject(new File(Commit.COMMITS_DIR, parentId), Commit.class);
            currentCommit = parentCommit;
            listOfParents = parentCommit.getParents();
        }
        // At last, print the initial commit.
        currentCommit.dump();
    }


    /**
     * Like log, except displays information about all commits ever made.
     * The order of the commits doesn't matter.
     */
    public static void globalLog() {
        List<String> commitList = plainFilenamesIn(Commit.COMMITS_DIR);
        if (commitList != null && !commitList.isEmpty()) {
            for (String commitId : commitList) {
                Commit commit = readObject(new File(Commit.COMMITS_DIR, commitId), Commit.class);
                commit.dump();
            }
        }
    }


    /**
     * Prints out the ids of all commits that have the given commit message, one per line.
     * If there are multiple such commits, it prints the ids out on separate lines.
     *
     * @param commitMessage the message to search for.
     */
    public static void find(String commitMessage) {
        // use a flag to judge if you find command found any commit with the given message.
        List<String> commitsList = plainFilenamesIn(Commit.COMMITS_DIR);
        boolean found = false;
        if (commitsList != null && !commitsList.isEmpty()) {
            for (String commitId : commitsList) {
                Commit commit = readObject(new File(Commit.COMMITS_DIR, commitId), Commit.class);
                String message = commit.getMessage();
                if (message.equals(commitMessage)) {
                    found = true;
                    System.out.println(commit.getId());
                }
            }
            if (!found) {
                message("Found no commit with that message.");
                System.exit(0);
            }
        }
    }


    /**
     * Displays what branches currently exist, and marks the current branch with a *.
     * Also displays what files have been staged for addition or removal.
     */
    public static void status() {
        printBranches();
        printIndex();
        printModifiedFiles();
        printUntrackedFiles();
    }


    /**
     * As a helper method to print the branches.
     */
    private static void printBranches() {
        System.out.println("=== Branches ===");
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        String currentBranch = readContentsAsString(HEAD_FILE);
        if (branchList != null && !branchList.isEmpty()) {
            for (String branch : branchList) {
                if (currentBranch.equals(branch)) {
                    System.out.println("*" + branch);
                } else {
                    System.out.println(branch);
                }
            }
        }
        System.out.println();
    }


    /**
     * As a helper method to print the index(stage area).
     */
    private static void printIndex() {
        Index index = readObject(INDEX_FILE, Index.class);
        Map<String, String> stageBlobs = index.getAddBlobs();
        Map<String, String> removeStageBlobs = index.getRemoveBlobs();
        System.out.println("=== Staged Files ===");
        for (Map.Entry<String, String> entry : stageBlobs.entrySet()) {
            String filename = entry.getKey();
            System.out.println(filename);
        }
        System.out.println();
        System.out.println("=== Removed Files ===");
        for (Map.Entry<String, String> entry : removeStageBlobs.entrySet()) {
            String filename = entry.getKey();
            System.out.println(filename);
        }
        System.out.println();
    }

    /**
     * As a helper method to print the modified files.
     */
    private static void printModifiedFiles() {
        // TODO: Find bugs here.
        String branch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        Map<String, String> currentBlob = currentCommit.getBlobMap();
        Index index = readObject(INDEX_FILE, Index.class);
        List<String> workingFiles = plainFilenamesIn(CWD);
        System.out.println("=== Modifications Not Staged For Commit ===");
        // There have 4 cases that a file "is modified but not staged".
        // 1. Tracked in the current commit, changed in the working directory, but not staged; (modified)
        // 2. Staged for addition, but with different contents than in working directory; (modified)
        // 3. Staged for addition, but deleted in the working directory; (deleted)
        // 4. Not staged for removal,
        // but tracked in the current commit and deleted from the working directory.
        // (deleted)
        if (workingFiles != null) {
            for (String filename : workingFiles) {
                File file = new File(CWD, filename);
                String content = readContentsAsString(file);
                String fileId = sha1(filename, content);
                // case 1
                if (!index.addContainsFile(filename) && !index.removeContainsFile(filename)
                        && currentCommit.containsFile(filename) && !currentCommit.containsId(fileId)) {
                    System.out.println(filename + " (modified)");
                } else if (index.addContainsFile(filename)) {
                    // case 2
                    if (!index.addContainsId(fileId) && file.exists()) {
                        System.out.println(filename + " (modified)");
                    } else if (!file.exists()) {
                        // case 3
                        System.out.println(filename + " (deleted)");
                    }
                }
            }
            for (Map.Entry<String, String> entry : currentBlob.entrySet()) {
                String filename = entry.getKey();
                File file = new File(join(CWD), filename);
                // case 4
                if (!index.removeContainsFile(filename) &&
                        currentCommit.containsFile(filename) && !file.exists()) {
                    System.out.println(filename + " (deleted)");
                }
            }
        }
        System.out.println();
    }


    /**
     * As a helper method to print the untracked files.
     */
    private static void printUntrackedFiles() {
        // files present in the working directory but neither staged for addition nor tracked.
        String branch = readContentsAsString(HEAD_FILE);
        List<String> workingFiles = plainFilenamesIn(CWD);
        List<String> blobFiles = plainFilenamesIn(Blob.BLOBS_DIR);
        Index index = readObject(INDEX_FILE, Index.class);
        // "Untracked Files” is for files present in the working directory
        // but neither staged for addition nor tracked
        // (only tracked by the parent commit (single))
        System.out.println("=== Untracked Files ===");
        if (workingFiles != null && !workingFiles.isEmpty()) {
            for (String filename : workingFiles) {
                Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                        readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
                if (!index.addContainsFile(filename) && !index.removeContainsFile(filename) &&
                        !currentCommit.containsFile(filename)) {
                    System.out.println(filename);
                }
            }
        }
        System.out.println();
    }


    /**
     * checkout the file or branch with the given name.
     *
     * @param arg      the name of the file or branch to check out.
     * @param isBranch whether the argument is a branch name or a file name.
     */
    public static void checkout(String arg, boolean isBranch) {
        String currentBranch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, currentBranch))), Commit.class);
        Index index = readObject(INDEX_FILE, Index.class);
        if (!isBranch) {
            // arg is filename
            boolean containsFile = currentCommit.containsFile(arg);
            if (!containsFile) {
                message("File does not exist in that commit.");
                System.exit(0);
            } else {
                Map<String, String> currentBlobs = currentCommit.getBlobMap();
                writeFileContent(arg, currentBlobs);
            }
        } else {
            // arg is branchName
            File branchFile = new File(BRANCH_DIR, arg);
            boolean containsBranchFile = branchFile.exists();
            if (!containsBranchFile) {
                message("No such branch exists.");
                System.exit(0);
            } else if (currentBranch.equals(arg)) {
                message("No need to checkout the current branch.");
                System.exit(0);
            }
            Commit newCommit = readObject(new File(Commit.COMMITS_DIR,
                    readContentsAsString(branchFile)), Commit.class);
            // update the index to match the new commit.
            Map<String, String> oldBlobs = currentCommit.getBlobMap();
            Map<String, String> newBLobs = newCommit.getBlobMap();
            fileOperation(oldBlobs, newBLobs);
            // update the HEAD file to point to the new branch.
            writeContents(HEAD_FILE, arg);
        }
    }


    /**
     * As a helper method to handle the file operation.
     *
     * @param oldBlobs the map of blobs in the old commit.
     * @param newBLobs the map of blobs in the new commit.
     */
    private static void fileOperation(Map<String, String> oldBlobs, Map<String, String> newBLobs) {
        // TODO: Find bugs here.
        // Three cases:
        // 1. The file is in both commits, but with different contents or same contents.
        // 2. The file is in the current commit but not in the new commit.
        // 3. The file is in the new commit but not in the current commit.
        // Use single loop to optimize the performance.
        Index index = readObject(INDEX_FILE, Index.class);
        if (!newBLobs.isEmpty()) {
            for (Map.Entry<String, String> newEntry : newBLobs.entrySet()) {
                String newFilename = newEntry.getKey();
                String newId = newEntry.getValue();
                File file = new File(join(CWD), newFilename);
                Blob blob = Blob.fromFile(newId);
                String content = blob.getContent();
                String id = oldBlobs.get(newFilename);
                // case 3
                if (id == null) {
                    if (!file.exists()) {
                        try {
                            boolean fileExists = file.createNewFile();
                            writeContents(file, content);
                        } catch (Exception e) {
                            System.err.println("Error: Could not create file.");
                        }
                    } else {
                        message("There is an untracked file in the way; " +
                                "delete it, or add and commit it first.");
                        System.exit(0);
                    }
                    // Only case 3 (checkout of a full branch) modifies the staging area:
                    // otherwise files scheduled for addition or removal remain so.
                    index.clearFile();
                } else {
                    // case 1
                    if (!id.equals(newId)) {
                        // update the file with the new content.
                        writeContents(file, content);
                    }
                    // if the file is as same as the old commit, do nothing.
                }
            }
            removeRedundantFiles(oldBlobs, newBLobs);
        } else {
            removeRedundantFiles(oldBlobs, newBLobs);
        }
    }

    /**
     * As a helper method to remove redundant files.
     *
     * @param oldBlobs the map of blobs in the old commit.
     * @param newBLobs the map of blobs in the new commit.
     */
    private static void removeRedundantFiles(Map<String, String> oldBlobs, Map<String, String> newBLobs) {
        for (Map.Entry<String, String> entry : oldBlobs.entrySet()) {
            String filename = entry.getKey();
            String newId = newBLobs.get(filename);
            if (newId == null) {
                // case 2
                // remove the files in the current commit but not in the new commit.
                boolean fileDeleted = new File(join(CWD), filename).delete();
            }
        }
    }


    /**
     * checkout the file with the given name at the given commit.
     *
     * @param commitId the id of the commit to check out.
     * @param filename the name of the file to check out.
     */
    public static void checkout(String commitId, String filename) {
        // Abbreviate commits with a unique prefix
        commitId = getCommitId(commitId);
        Commit commitObject = readObject(new File(Commit.COMMITS_DIR, commitId), Commit.class);
        Map<String, String> currentBlobs = commitObject.getBlobMap();
        if (!commitObject.containsFile(filename)) {
            message("File does not exist in that commit.");
            System.exit(0);
        } else {
            writeFileContent(filename, currentBlobs);

        }
    }

    private static String getCommitId(String commitId) {
        boolean isAbbreviated = commitId.length() < 40;
        List<String> commitsList = plainFilenamesIn(Commit.COMMITS_DIR);
        if (commitsList != null && !commitsList.isEmpty()) {
            for (String commit : commitsList) {
                if (commit.startsWith(commitId)) {
                    isAbbreviated = true;
                    commitId = commit;
                    break;
                }
                isAbbreviated = false;
            }
            if (!isAbbreviated) {
                message("No commit with that id exists.");
                System.exit(0);
            }
        }
        return commitId;
    }


    /**
     * As a helper method to write the content of the file to the file.
     *
     * @param filename     the name of the file to write to.
     * @param currentBlobs the map of blobs in the current commit.
     */
    private static void writeFileContent(String filename, Map<String, String> currentBlobs) {
        String id = currentBlobs.get(filename);
        File file = new File(join(CWD), filename);
        Blob blob = Blob.fromFile(id);
        String content = blob.getContent();
        if (file.exists()) {
            // overwriting the version of the file that’s already there if there's one.
            writeContents(file, content);
        } else {
            try {
                boolean fileExist = file.createNewFile();
                writeContents(file, content);
            } catch (Exception e) {
                System.err.println("Error: Could not create file.");
            }
        }
    }


    /**
     * Creates a new branch with the given name.
     *
     * @param branchName the name of the new branch.
     */
    public static void branch(String branchName) {
        String currentBranch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, currentBranch))), Commit.class);
        // create a new branch if it doesn't exist.
        File branchFile = new File(BRANCH_DIR, branchName);
        if (!branchFile.exists() && BRANCH_DIR.exists()) {
            try {
                boolean branchExist = branchFile.createNewFile();
                writeContents(branchFile, currentCommit.getId());
            } catch (Exception e) {
                System.err.println("Error: Could not create branch.");
            }
        } else {
            message("A branch with that name already exists.");
            System.exit(0);
        }
    }


    /**
     * Deletes the branch with the given name.
     *
     * @param branchName the name of the branch to delete.
     */
    public static void rmBranch(String branchName) {
        String currentBranch = readContentsAsString(HEAD_FILE);
        File branchFile = new File(BRANCH_DIR, branchName);
        if (branchFile.exists() && BRANCH_DIR.exists()) {
            if (currentBranch.equals(branchName)) {
                message("Cannot remove the current branch.");
                System.exit(0);
            }
            boolean branchExist = branchFile.delete();
        } else {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
    }


    /**
     * Resets the current branch to the given commit.
     * Checks out all the files tracked by the given commit.
     * Removes tracked files that aren't present in that commit.
     * Also moves the current branch’s head to that commit node.
     *
     * @param commitId the id of the commit to reset to.
     */
    public static void reset(String commitId) {
        String currentBranch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, currentBranch))), Commit.class);
        Index index = readObject(INDEX_FILE, Index.class);
        commitId = getCommitId(commitId);
        Commit newCommit = readObject(new File(Commit.COMMITS_DIR, commitId), Commit.class);
        Map<String, String> oldBlobs = currentCommit.getBlobMap();
        Map<String, String> newBlobs = newCommit.getBlobMap();
        // checkout arbitrary commit.
        fileOperation(oldBlobs, newBlobs);
        // update the HEAD file to point to the new commit.
        List<String> branchList = plainFilenamesIn(BRANCH_DIR);
        if (branchList != null) {
            for (String branch : branchList) {
                if (currentBranch.equals(branch)) {
                    File branchFile = join(BRANCH_DIR, branch);
                    writeContents(branchFile, commitId);
                    writeContents(HEAD_FILE, branch);
                }
            }
        }
        index.clearFile();
    }

    /**
     * Merges files from the given branch into the current branch
     *
     * @param branchName the name of the branch to merge.
     */
    public static void merge(String branchName) {
        // step1:find the split point
        Index index = readObject(INDEX_FILE, Index.class);
        // If there are staged additions or removals present
        if (!index.addIsEmpty() || !index.removeIsEmpty()) {
            message("You have uncommitted changes.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(HEAD_FILE);
        File branchFile = new File(BRANCH_DIR, currentBranch);
        File mergeBranchFile = new File(BRANCH_DIR, branchName);
        //  If a branch with the given name doesn't exist
        if (!mergeBranchFile.exists()) {
            message("A branch with that name does not exist.");
            System.exit(0);
        }
        // If attempting to merge a branch with itself
        if (currentBranch.equals(branchName)) {
            message("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(branchFile)), Commit.class);
        Commit mergeCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(mergeBranchFile)), Commit.class);
        Commit mutualParentCommit = findMergeBase(currentCommit, mergeCommit);
        //  If the split point is the same commit as the given branch, then we do nothing;
        //  the merge is complete
        if (mutualParentCommit.getId().equals(mergeCommit.getId())) {
            message("Given branch is an ancestor of the current branch.");
            System.exit(0);
        } else if (mutualParentCommit.getId().equals(currentCommit.getId())) {
            // If the split point is the current branch,
            // then the effect is to check out the given branch
            checkout(branchName, true);
            message("Current branch fast-forwarded.");
            System.exit(0);
        } else {
            // step2: merge the files
            fileOperation(mutualParentCommit, currentCommit, mergeCommit);
            // step3: create a new commit
            index = readObject(INDEX_FILE, Index.class);
            Map<String, String> finalBlobs = dealWithStageAndCommitBlobs(index, currentCommit);
            List<String> parents = new ArrayList<>();
            parents.add(currentCommit.getId());
            parents.add(mergeCommit.getId());
            Commit newCommit = new Commit("Merged " + branchName + " into " + currentBranch + ".",
                    new Date(), parents, finalBlobs);
            // step4: update the HEAD file to point to the new commit.
            writeContents(branchFile, newCommit.getId());
            index.clearFile();
        }
    }

    private static Map<String, String> dealWithStageAndCommitBlobs(Index index, Commit currentCommit) {
        Map<String, String> stageBlobs = index.getAddBlobs();
        Map<String, String> removeBlobs = index.getRemoveBlobs();
        Map<String, String> newBlobs = handleBlobs(stageBlobs, currentCommit.getBlobMap());
        return handleRemoveBlobs(removeBlobs, newBlobs);
    }

    /**
     * As a helper method to merge the files.
     *
     * @param mutualCommit  the common ancestor commit.
     * @param currentCommit the current commit.
     * @param mergeCommit   the merge commit.
     *                      &#064;source   from <a href="https://zhuanlan.zhihu.com/p/533852291">...</a> and
     *                      <a href="https://www.youtube.com/watch?v=JR3OYCMv9b4&t=929s">...</a> ,which use the ideology of using
     *                      a <value(id),key(filename)> map to represent the files in the three commits.
     */
    private static void fileOperation(Commit mutualCommit,
                                      Commit currentCommit,
                                      Commit mergeCommit) {
        // first: find all the files that are in different branches
        Index index = readObject(INDEX_FILE, Index.class);
        // use new keywords to avoid modifying the original maps
        Map<String, String> mutualBlobs = new TreeMap<>(mutualCommit.getBlobMap());
        Map<String, String> currentBlobs = new TreeMap<>(currentCommit.getBlobMap());
        Map<String, String> mergeBlobs = new TreeMap<>(mergeCommit.getBlobMap());
        Map<String, String> allBlobs = getAllBlobs(mutualBlobs, currentBlobs, mergeBlobs);
        // Second: merge the files
        // There are 8 cases:
        // 1. Modified in merge branch but not in current branch,
        // the result is the merge branch's version.
        // 2. Modified in current branch but not in merge branch,
        // the result is the current branch's version.
        // 3. Modified in both branches:
        //     3.1.
        //     The two versions are the same, the result is the same version.
        //     3.2.
        //     The two versions are different, the result is the conflicted version.
        // 4. Not in split point nor in merge branch, but in current branch,
        // the result is the current branch's version.
        // 5. Not in split point nor in the current branch,
        // but in merge branch, the result is the merge branch's version.
        // 6. Unmodified in the current branch but not present in the merge branch,
        // the result is the merge branch's version.(removed)
        // 7. Unmodified in the merge branch but not present in the current branch,
        // the result is the current branch's version.(remain removed)

        // The only way we distinguish files is by theirs id.
        // Because the files maybe deleted first,so we need
        // to check if the files are deleted in the current branch
        // or the merge branch or in the split point.
        // the order of the if-else statements matters.
        // beacause
        for (Map.Entry<String, String> entry : allBlobs.entrySet()) {
            String filename = entry.getKey();
            String id = entry.getValue();
            // Case 4,
            // the file isn't in the merge branch and not in the split point,
            // but in the current branch.
            // The file isn't new for the current branch.
            // So we do nothing.
            if (!present(mutualCommit, filename, id) && !present(mergeCommit, filename, id)
                    && present(currentCommit, filename, id)) {
                continue;
            }
            // Case 5,
            // the file isn't in the current branch and not in the split point,
            // but in the merge branch.
            // The file is new for the current branch.
            // So we create a new file and add it to the index.
            if (!present(mutualCommit, filename, id) && !present(currentCommit, filename, id)
                    && present(mergeCommit, filename, id)) {
                checkFiles(currentCommit, mergeCommit);
                Blob blob = Blob.fromFile(id);
                String content = blob.getContent();
                File file = new File(CWD, filename);
                try {
                    file.createNewFile();
                    writeContents(file, content);
                    index.addAdd(blob);
                    continue;
                } catch (Exception e) {
                    System.err.println("Error: Could not create file.");
                }
            }
            // Case 6,
            // the file is unmodified in the current branch,
            // but not present in the merge branch.
            // So we remove it from the CWD and the index.
            if (present(mutualCommit, filename, id) && !modified(currentCommit, filename, id)
                    && !present(mergeCommit, filename, id)) {

                checkFiles(currentCommit, mergeCommit);
                File file = new File(CWD, filename);
                file.delete();
                Blob blob = Blob.fromFile(id);
                index.removeAdd(blob);
                continue;
            }
            // Case 7
            // the file is unmodified in the merge branch,
            // but not present in the current branch.
            // So we do nothing.
            if (present(mutualCommit, filename, id) && !modified(mergeCommit, filename, id)
                    && !present(currentCommit, filename, id)) {
                continue;
            }
            // all possible cases about case 3 are following:
            // Case 3.1
            // (not in the split point, but in both branches, and have the same contents(same id))
            if (!present(mutualCommit, filename, id) && present(currentCommit, filename, id)
                    && present(mergeCommit, filename, id)
                    && currentCommit.getBlobMap().get(filename).equals(mergeCommit.getBlobMap().get(filename))) {
                try {
                    Blob blob = Blob.fromFile(id);
                    String content = blob.getContent();
                    File file = new File(CWD, filename);
                    file.createNewFile();
                    writeContents(file, content);
                    continue;
                } catch (Exception e) {
                    System.err.println("Error: Could not merge file.");
                }
            }
            // Case 3.2
            // (not in the split point, but in both branches, and have different contents(different id))
            // The file is modified in both branches,
            // and the two versions are different.
            // The result is the conflicted version.
            // We handle the conflict in the next step.
            if (!present(mutualCommit, filename, id) && present(currentCommit, filename, id)
                    && present(mergeCommit, filename, id)
                    && !currentCommit.getBlobMap().get(filename).equals(mergeCommit.getBlobMap().get(filename))) {
                message("Encountered a merge conflict.");
                handleConflicts(currentCommit, mergeCommit, filename);
                continue;
            }
            // Case 3.1
            // (in the split point, but not in both branches, they are deleted in both branches)
            // So we do nothing.
            if (present(mutualCommit, filename, id) && !present(currentCommit, filename, id)
                    && !present(mergeCommit, filename, id)) {
                continue;
            }

            // case 3.1 not changed
            if (!modified(currentCommit, filename, id) && !modified(mergeCommit, filename, id)) {
                // They both didn't modify the files
                // The result is the same version.
                // Do nothing.
                continue;
            }
            // case 3.1 modified in the same way
            if (modified(currentCommit, filename, id) && modified(mergeCommit, filename, id)
                    && currentCommit.getBlobMap().get(filename).equals(mergeCommit.getBlobMap().get(filename))) {
                // They both modified the file in the same way.
                // The result is the same version.
                // Do nothing.
                continue;
            }
            // Case 3.2
            //  file is in the split point,
            //  but not present in the current branch and modified in the merge branch.
            if (present(mutualCommit, filename, id) && !present(currentCommit, filename, id)
                    && modified(mergeCommit, filename, id)) {
                // The file is modified in the merge branch,
                // and deleted in the current branch.
                // The result is the conflicted version.
                message("Encountered a merge conflict.");
                handleConflicts(currentCommit, mergeCommit, filename);
                continue;
            }
            // Case 3.2
            //  file is in the split point,
            //  but not present in the merge branch and modified in the current branch.
            if (present(mutualCommit, filename, id) && !present(mergeCommit, filename, id)
                    && modified(currentCommit, filename, id)) {
                message("Encountered a merge conflict.");
                handleConflicts(currentCommit, mergeCommit, filename);
                continue;
            }
            // Case 3.2
            //  file is in the split point,
            //  but modified in both branches in different way.
            if (present(mutualCommit, filename, id) && modified(currentCommit, filename, id)
                    && modified(mergeCommit, filename, id)) {
                message("Encountered a merge conflict.");
                handleConflicts(currentCommit, mergeCommit, filename);
                continue;
            }
            // Case 1
            // modified(not including deleted) in merge branch but not in current branch,
            // the deletion is handled in case 6.
            // → The merge branch's version.
            if (modified(mergeCommit, filename, id) && !modified(currentCommit, filename, id)) {
                checkFiles(currentCommit, mergeCommit);
                String newId = mergeCommit.getBlobMap().get(filename);
                Blob blob = Blob.fromFile(newId);
                String content = blob.getContent();
                File file = new File(CWD, filename);
                writeContents(file, content);
                index.addAdd(blob);
                continue;
            }
            // Case 2
            // modified in current branch but not in merge branch,
            // the deletion case is handled in case 7.
            // → The current branch's version.
            if (modified(currentCommit, filename, id) && !modified(mergeCommit, filename, id)) {
                // Do nothing.
                continue;
            }
        }
        index.saveFile();
    }


    /**
     * As a helper method to check if a commit has modified files.
     *
     * @param commit the commit to check.
     * @return true if the commit has modified files, false otherwise.
     */
    private static boolean modified(Commit commit, String filename, String id) {
        return !commit.containsFile(filename) || !commit.containsId(id);
    }

    private static boolean present(Commit commit, String filename, String id) {
        return commit.containsId(id) && commit.containsFile(filename);
    }

    private static void handleConflicts(Commit currentCommit, Commit mergeCommit,
                                        String filename) {
        String currentId = currentCommit.getBlobMap().get(filename);
        String mergeId = mergeCommit.getBlobMap().get(filename);
        if (currentId == null) {
            Blob mergeBlob = Blob.fromFile(mergeId);
            String mergeContent = mergeBlob.getContent();
            File file = new File(CWD, filename);
            String content = "<<<<<<< HEAD" + "\n" + "=======\n"
                    + mergeContent + ">>>>>>>" + "\n";
            writeContents(file, content);
        } else if (mergeId == null) {
            Blob currentBlob = Blob.fromFile(currentId);
            String currentContent = currentBlob.getContent();
            File file = new File(CWD, filename);
            String content = "<<<<<<< HEAD" + "\n" + currentContent + "=======\n"
                    + ">>>>>>>" + "\n";
            writeContents(file, content);
        } else {
            Blob currentBlob = Blob.fromFile(currentId);
            Blob mergeBlob = Blob.fromFile(mergeId);
            String currentContent = currentBlob.getContent();
            String mergeContent = mergeBlob.getContent();
            File file = new File(CWD, filename);
            String content = "<<<<<<< HEAD" + "\n" + currentContent + "=======\n"
                    + mergeContent + ">>>>>>>" + "\n";
            writeContents(file, content);
        }
    }

    /**
     * As a helper method to get all the blobs in the three commits.
     *
     * @param mutualBlobs  the map of blobs in the common ancestor commit.
     * @param currentBlobs the map of blobs in the current commit.
     * @param mergeBlobs   the map of blobs in the merge commit.
     * @return the map of all the blobs in the three commits.
     */
    private static Map<String, String> getAllBlobs(Map<String, String> mutualBlobs,
                                                   Map<String, String> currentBlobs,
                                                   Map<String, String> mergeBlobs) {
        Map<String, String> allBlobs = new HashMap<>(mutualBlobs);
        allBlobs.putAll(currentBlobs);
        allBlobs.putAll(mergeBlobs);
        return allBlobs;
    }

    private static void checkFiles(Commit currentCommit, Commit mergeCommit) {
        // If the merge overwrites or deletes an untracked file in the current commit
        String branch = readContentsAsString(HEAD_FILE);
        List<String> workingFiles = plainFilenamesIn(CWD);
        Index index = readObject(INDEX_FILE, Index.class);
        if (workingFiles != null && !workingFiles.isEmpty()) {
            for (String filename : workingFiles) {
                if (mergeCommit.containsFile(filename) && !currentCommit.containsFile(filename)) {
                    // If the file is modified in the current commit,
                    message("There is an untracked file in the way; delete it, " +
                            "or add and commit it first.");
                    System.exit(0);
                }
            }
        }
    }


    /**
     * As a helper method to find the split point for the merge.
     *
     * @return the commit that is the split point for the merge.
     * from CHATGPT, use an algorithm called "Bidirectional Breadth-First Search, BiBFS"
     * to find the split point for the merge.
     */
    private static Commit findMergeBase(Commit commit1, Commit commit2) {
        if (commit1 == null || commit2 == null) {
            return null;
        }
        // initialize two iterators
        HashSet<String> visited1 = new HashSet<>(); // Used to record nodes that iterator 1 has visited
        HashSet<String> visited2 = new HashSet<>(); //Used to record nodes that iterator 2 has visited
        Queue<String> q1 = new LinkedList<>(); // Queue for iterator 1
        Queue<String> q2 = new LinkedList<>(); // Queue for iterator 2

        // Add the two commits to their respective queues
        q1.add(commit1.getId());
        q2.add(commit2.getId());

        // mark the two commits as visited
        visited1.add(commit1.getId());
        visited2.add(commit2.getId());

        // Alternate traversal
        while (!q1.isEmpty() || !q2.isEmpty()) {
            // Iterator 1 takes one step in traversal
            if (!q1.isEmpty()) {
                Commit currentCommit = readObject(new File(Commit.COMMITS_DIR, q1.poll()), Commit.class);
                List<String> parents = currentCommit.getParents();
                // Iterator 1 traverses along the parent commits
                for (String parent : parents) {
                    // If the parent commit is already in the visited nodes of iterator 2.
                    if (visited2.contains(parent)) {
                        return readObject(new File(Commit.COMMITS_DIR, parent), Commit.class);
                    }
                    // Otherwise continue traversing
                    else if (!visited1.contains(parent)) {
                        q1.add(parent);
                        visited1.add(parent);
                    }
                }
            }
            // Iterator 2 takes one step in traversal
            if (!q2.isEmpty()) {
                Commit currentCommit = readObject(new File(Commit.COMMITS_DIR, q2.poll()), Commit.class);
                List<String> parents = currentCommit.getParents();
                // Iterator 2 traverses along the parent commits
                for (String parent : parents) {
                    // If the parent commit is already in the visited nodes of iterator 1
                    if (visited1.contains(parent)) {
                        return readObject(new File(Commit.COMMITS_DIR, parent), Commit.class);
                    } else if (!visited2.contains(parent)) {
                        q2.add(parent);
                        visited2.add(parent);
                    }
                }

            }
        }
        // If no common ancestor is found, return null
        return null;
    }

}


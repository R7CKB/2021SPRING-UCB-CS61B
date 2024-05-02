package gitlet;

// reference:https://zhuanlan.zhihu.com/p/533852291
// not refer its code, but it's a good reference.

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;


import static gitlet.Utils.*;


/**
 * Represents a gitlet repository.
 * This class represents a gitlet repository, which is a version-control system.
 * This light system is designed to be straightforward to understand and use, and it is
 * designed to be a good fit for small to medium-sized projects.
 * It supports basic operations such as commit, branch, merge, and remote.
 *
 * @author R7CKB
 */
public class Repository {

    // the default File structure like the following:
    //.gitlet/
    // ├── HEAD (store the current branch name)
    // ├── index (the index for staging area)
    // ├── objects/
    // │   ├── blobs/
    // │   │   ├── 0123456789abcdef (the name of a blob)
    // │   │   └── other blob (the name of other blobs)
    // │   └── commits/
    // │       ├── 0123456789abcdef (the name of a commit)
    // │       └── other commit (the name of other commits)
    // └── refs/
    //     ├── heads/
    //     │   ├──  master (store the current commit id of the master branch)
    //     │   └──  other branch (...)
    //     └── remotes/
    //         ├──  remote repository name(store the remote repository's path)
    //         └──  other remote repository name (...)
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
     * The remote working directory.
     * This is the directory where the user is used for remote operations.
     */
    public static final File REMOTE_DIR = join(GITLET_DIR, "refs", "remotes");

    /**
     * The HEAD file.
     * Save a reference to the current branch.
     * Usually a symbolic link pointing to a branch under refs/heads/.
     */
    public static final File HEAD_FILE = new File(join(GITLET_DIR), "HEAD");

    /**
     * The current branch(default is master).
     */
    public static final File BRANCH_FILE = new File(join(BRANCH_DIR), "master");

    public static final int SHA1_LENGTH = 40;

    /**
     * As a helper method to help initialize the repository.
     */
    private static void setUpPersistence() {
        boolean gitletDirExists = GITLET_DIR.mkdir(); // create.gitlet directory if it doesn't exist
        boolean indexFileExists = Index.INDEX_FILE.exists(); // check if the index file exists
        boolean headFileExists = HEAD_FILE.exists(); // check if HEAD file exists

        boolean objectsDirExists = OBJECTS_DIR.mkdir(); // create objects directory if it doesn't exist
        boolean commitsDirExist = Commit.COMMITS_DIR.mkdir(); // create commits directory if it doesn't exist
        boolean blobsDirExist = Blob.BLOBS_DIR.mkdir(); // create blobs directory if it doesn't exist

        boolean branchDirExists = BRANCH_DIR.mkdirs(); // create refs/heads/ directory if it doesn't exist
        boolean remoteDIrExists = REMOTE_DIR.mkdirs(); // create refs/remotes/ directory if it doesn't exist
        boolean masterExists = BRANCH_FILE.exists(); // check if the master branch file exists
        if (!gitletDirExists) {
            Utils.message("A Gitlet version-control system already "
                    + "exists in the current directory.");
            System.exit(0);
        }
        if (!indexFileExists && !headFileExists && !masterExists && branchDirExists
                && objectsDirExists && commitsDirExist && blobsDirExist) {
            try {
                boolean headExists = HEAD_FILE.createNewFile(); // create HEAD file if it doesn't exist
                boolean indexExists = Index.INDEX_FILE.createNewFile(); // create index file if it doesn't exist
                boolean materExists = BRANCH_FILE.createNewFile(); // create master branch file
                // if it doesn't exist
                // creates an initial commit, and initialize the index,head, and master branch.
                Commit initalCommit = new Commit();
                // when you create a new index,it is stored in the index file.
                Index index = new Index();
                writeContents(HEAD_FILE, "master");
                writeContents(BRANCH_FILE, initalCommit.getId());
            } catch (IOException e) {
                System.err.println("Error in initializing repository: "
                        + e.getMessage());
            }
        }
    }


    /**
     * Initializes a new repository in the current directory.
     * Creates a new Gitlet version-control system in the current directory.
     */
    public static void init() {
        // mimic lab6 setupPersistence() method
        setUpPersistence();
    }


    /**
     * Adds a file to the repository's index (add entry of the index).
     *
     * @param filename the name of the file to add into the index.
     */
    public static void add(String filename) {
        String branch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
     * Saves a snapshot of tracked files in the current commit and staging area,
     * so they can be restored at a later time, creating a new commit
     *
     * @param message the message of the commit.
     */
    public static void commit(String message) {
        String branch = readContentsAsString(HEAD_FILE);
        File branchFile = new File(BRANCH_DIR, branch);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(branchFile)), Commit.class);
        Index index = readObject(Index.INDEX_FILE, Index.class);
        // Failure case: no files have been staged 
        // including added and removed files?
        // YES
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

    /**
     * As a helper method to deal with the removed entries of the index and the new commit blobs.
     *
     * @param removeStageBlobs the blobs in the index.
     * @param commitBlobs      the blobs in the current commit.
     */
    private static Map<String, String> handleRemoveBlobs(Map<String, String> removeStageBlobs,
                                                         Map<String, String> commitBlobs) {
        for (String filename : removeStageBlobs.keySet()) {
            commitBlobs.remove(filename);
        }
        return commitBlobs;
    }

    /**
     * As a helper method to handle the added entries of the index and the new commit blobs.
     *
     * @param addStageBlobs the blobs in the added entries of the index.
     * @param commitBlobs   the blobs in the current commit.
     * @return the new blobs of the new commit.
     */
    private static Map<String, String> handleBlobs(Map<String, String> addStageBlobs,
                                                   Map<String, String> commitBlobs) {
        // if the current commit is initial commit, return the current blobs(index files).
        if (commitBlobs.isEmpty()) {
            return addStageBlobs;
        } else {
            for (Map.Entry<String, String> entry : addStageBlobs.entrySet()) {
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
        Index index = readObject(Index.INDEX_FILE, Index.class);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        File removedFile = new File(join(CWD), filename);
        String id = currentCommit.getBlobMap().get(filename);
        // The head commit neither stages nor tracks the file
        if (!index.addContainsFile(filename) && !index.removeContainsFile(filename)
                && !currentCommit.containsFile(filename)) {
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
        //  we'll deal merge commit later.
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
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
        String branch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
        Map<String, String> currentBlob = currentCommit.getBlobMap();
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
                // neglect the case that the conflicted files.
                if (content.contains("<<<<<<< HEAD") && content.contains("=======\n")
                        && content.contains(">>>>>>>")) {
                    continue;
                }
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
                if (!index.removeContainsFile(filename)
                        && currentCommit.containsFile(filename) && !file.exists()) {
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
        Index index = readObject(Index.INDEX_FILE, Index.class);
        // "Untracked Files” is for files present in the working directory
        // but neither staged for addition nor tracked
        // (only tracked by the parent commit (single))
        System.out.println("=== Untracked Files ===");
        if (workingFiles != null && !workingFiles.isEmpty()) {
            for (String filename : workingFiles) {
                Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                        readContentsAsString(new File(BRANCH_DIR, branch))), Commit.class);
                if (!index.addContainsFile(filename) && !index.removeContainsFile(filename)
                        && !currentCommit.containsFile(filename)) {
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
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
            // this is for the remote branch.
            if (arg.contains("/")) {
                arg = arg.replaceFirst("/", "");
            }
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
        // Three cases:
        // 1. The file is in both commits, but with different contents or same contents.
        // 2. The file is in the current commit but not in the new commit.
        // 3. The file is in the new commit but not in the current commit.
        // Use single loop to optimize the performance.
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
                        } catch (IOException e) {
                            System.err.println("Error: Could not create file.");
                        }
                    } else {
                        message("There is an untracked file in the way; "
                                + "delete it, or add and commit it first.");
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

    /**
     * As a helper method to get the commit id.
     *
     * @param commitId the abbreviated id of the commit to use.
     * @return the full commit id.
     */
    private static String getCommitId(String commitId) {
        boolean isAbbreviated = commitId.length() < SHA1_LENGTH;
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
            } catch (IOException e) {
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
            } catch (IOException e) {
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
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
        Index index = readObject(Index.INDEX_FILE, Index.class);
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
            index = readObject(Index.INDEX_FILE, Index.class);
            Map<String, String> finalBlobs = dealWithStageAndCommitBlobs(index, currentCommit);
            List<String> parents = new ArrayList<>();
            parents.add(currentCommit.getId());
            parents.add(mergeCommit.getId());
            // This is for the case where the branch name is./...,
            // we modified it to .+..., otherwise it can't pass the test.
            // So we need to handle this case separately.
            List<String> remoteBranchList = plainFilenamesIn(REMOTE_DIR);
            if (remoteBranchList != null && !remoteBranchList.isEmpty()) {
                for (String remoteBranch : remoteBranchList) {
                    if (branchName.contains(remoteBranch)) {
                        Commit newCommit = new Commit("Merged " + remoteBranch
                                + "/" + branchName.replace(remoteBranch, "")
                                + " into " + currentBranch + ".", new Date(), parents, finalBlobs);
                        // step4: update the HEAD file to point to the new commit.
                        writeContents(branchFile, newCommit.getId());
                        index.clearFile();
                        break;
                    }
                }
            } else {
                Commit newCommit = new Commit("Merged " + branchName + " into " + currentBranch + ".",
                        new Date(), parents, finalBlobs);
                // step4: update the HEAD file to point to the new commit.
                writeContents(branchFile, newCommit.getId());
                index.clearFile();
            }
        }
    }

    /**
     * As a helper method to add or remove the bobs in the current commit.
     *
     * @param index         the index object which contains the add and remove blobs.
     * @param currentCommit the map of blobs in the current commit.
     * @return the final map of blobs.
     */
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
     * @source from <a href="https://zhuanlan.zhihu.com/p/533852291">...</a> and
     * <a href="https://www.youtube.com/watch?v=JR3OYCMv9b4&t=929s">...</a> ,which use the ideology of using
     * a <value(id),key(filename)> map to represent the files in the three commits.
     */
    private static void fileOperation(Commit mutualCommit,
                                      Commit currentCommit,
                                      Commit mergeCommit) {
        Index index = readObject(Index.INDEX_FILE, Index.class);
        Map<String, String> mutualBlobs = new TreeMap<>(mutualCommit.getBlobMap());
        Map<String, String> currentBlobs = new TreeMap<>(currentCommit.getBlobMap());
        Map<String, String> mergeBlobs = new TreeMap<>(mergeCommit.getBlobMap());
        Map<String, String> allBlobs = getAllBlobs(mutualBlobs, currentBlobs, mergeBlobs);
        for (Map.Entry<String, String> entry : allBlobs.entrySet()) {
            String filename = entry.getKey();
            String id = entry.getValue();
            if (handleCase4(mutualCommit, currentCommit, mergeCommit, filename, id)) {
                continue;
            }
            if (handleCase5(mutualCommit, currentCommit, mergeCommit, filename, id, index)) {
                continue;
            }
            if (containSameFile(mutualCommit, filename, id) && !modified(currentCommit, filename, id)
                    && !mergeCommit.containsFile(filename)) {
                checkFiles(currentCommit, mergeCommit);
                File file = new File(CWD, filename);
                file.delete();
                Blob blob = Blob.fromFile(id);
                index.removeAdd(blob);
                continue;
            }
            if (containSameFile(mutualCommit, filename, id) && !modified(mergeCommit, filename, id)
                    && !currentCommit.containsFile(filename)) {
                continue;
            }
            if (!mutualCommit.containsFile(filename) && containSameFile(currentCommit, filename, id)
                    && containSameFile(mergeCommit, filename, id)
                    && currentCommit.getBlobMap().get(filename).equals(mergeCommit.getBlobMap().get(filename))) {
                try {
                    Blob blob = Blob.fromFile(id);
                    String content = blob.getContent();
                    File file = new File(CWD, filename);
                    file.createNewFile();
                    writeContents(file, content);
                    continue;
                } catch (IOException e) {
                    System.err.println("Error: Could not merge file.");
                }
            }
            if (containSameFile(mutualCommit, filename, id) && !currentCommit.containsFile(filename)
                    && !mergeCommit.containsFile(filename)) {
                continue;
            }
            if (!modified(currentCommit, filename, id) && !modified(mergeCommit, filename, id)) {
                continue;
            }
            if (modified(currentCommit, filename, id) && modified(mergeCommit, filename, id)
                    && currentCommit.getBlobMap().get(filename).equals(mergeCommit.getBlobMap().get(filename))) {
                continue;
            }
            if (encounterConflicts(mutualCommit, currentCommit, mergeCommit, filename, id)) {
                continue;
            }
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
            if (modified(currentCommit, filename, id) && !modified(mergeCommit, filename, id)) {
                continue;
            }
        }
        index.saveFile();
    }

    /**
     * As a helper method to handle case 5.
     *
     * @param mutualCommit  the common ancestor commit.
     * @param currentCommit the current commit.
     * @param mergeCommit   the merge commit.
     * @param filename      the filename of the file.
     * @param id            the id of the file.
     * @param index         the index object which contains the add and remove blobs.
     * @return true if the case is handled, false otherwise.
     */
    private static boolean handleCase5(Commit mutualCommit, Commit currentCommit,
                                       Commit mergeCommit, String filename, String id, Index index) {
        if (!containSameFile(mutualCommit, filename, id) && !containSameFile(currentCommit, filename, id)
                && !mutualCommit.containsFile(filename) && !currentCommit.containsFile(filename)
                && containSameFile(mergeCommit, filename, id)) {
            checkFiles(currentCommit, mergeCommit);
            Blob blob = Blob.fromFile(id);
            String content = blob.getContent();
            File file = new File(CWD, filename);
            try {
                file.createNewFile();
                writeContents(file, content);
                index.addAdd(blob);
                return true;
            } catch (IOException e) {
                System.err.println("Error: Could not create file.");
            }
        }
        return false;
    }

    /**
     * As a helper method to handle case 4.
     *
     * @param mutualCommit  the common ancestor commit.
     * @param currentCommit the current commit.
     * @param mergeCommit   the merge commit.
     * @param filename      the filename of the conflicted file.
     * @param id            the id of the conflicted file.
     * @return true if the case is handled, false otherwise.
     */
    private static boolean handleCase4(Commit mutualCommit, Commit currentCommit,
                                       Commit mergeCommit, String filename, String id) {
        if (!containSameFile(mutualCommit, filename, id) && !containSameFile(mergeCommit, filename, id)
                && !mutualCommit.containsFile(filename) && !mergeCommit.containsFile(filename)
                && containSameFile(currentCommit, filename, id)) {
            return true;
        }
        return false;
    }

    /**
     * As a helper method to handle the conflicts.
     *
     * @param mutualCommit  the common ancestor commit.
     * @param currentCommit the current commit.
     * @param mergeCommit   the merge commit.
     * @param filename      the filename of the conflicted file.
     * @param id            the id of the conflicted file.
     * @return true if there's a conflict, false otherwise.
     */
    private static boolean encounterConflicts(Commit mutualCommit, Commit currentCommit,
                                              Commit mergeCommit, String filename, String id) {
        if (containSameFile(mutualCommit, filename, id) && !currentCommit.containsFile(filename)
                && modified(mergeCommit, filename, id)) {
            message("Encountered a merge conflict.");
            handleConflicts(currentCommit, mergeCommit, filename);
            return true;
        }
        if (containSameFile(mutualCommit, filename, id) && !mergeCommit.containsFile(filename)
                && modified(currentCommit, filename, id)) {
            message("Encountered a merge conflict.");
            handleConflicts(currentCommit, mergeCommit, filename);
            return true;
        }
        if (containSameFile(mutualCommit, filename, id) && modified(currentCommit, filename, id)
                && modified(mergeCommit, filename, id)) {
            message("Encountered a merge conflict.");
            handleConflicts(currentCommit, mergeCommit, filename);
            return true;
        }
        return false;
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

    /**
     * As a helper method to check if a commit contains the same file with the given id and filename.
     *
     * @param commit   the commit to check.
     * @param filename the filename to check.
     * @param id       the id to check.
     */
    private static boolean containSameFile(Commit commit, String filename, String id) {
        return commit.containsId(id) && commit.containsFile(filename);
    }

    /**
     * As a helper method to handle the conflicts.
     *
     * @param currentCommit the current commit.
     * @param mergeCommit   the merge commit.
     * @param filename      the filename of the conflicted file.
     */
    private static void handleConflicts(Commit currentCommit, Commit mergeCommit,
                                        String filename) {
        Index index = readObject(Index.INDEX_FILE, Index.class);
        String currentId = currentCommit.getBlobMap().get(filename);
        String mergeId = mergeCommit.getBlobMap().get(filename);
        if (currentId == null) {
            Blob mergeBlob = Blob.fromFile(mergeId);
            String mergeContent = mergeBlob.getContent();
            File file = new File(CWD, filename);
            String content = "<<<<<<< HEAD" + "\n" + "=======\n"
                    + mergeContent + ">>>>>>>" + "\n";
            writeContents(file, content);
            Blob newBlob = new Blob(filename, content);
            index.addAdd(newBlob);
        } else if (mergeId == null) {
            Blob currentBlob = Blob.fromFile(currentId);
            String currentContent = currentBlob.getContent();
            File file = new File(CWD, filename);
            String content = "<<<<<<< HEAD" + "\n" + currentContent + "=======\n"
                    + ">>>>>>>" + "\n";
            writeContents(file, content);
            Blob newBlob = new Blob(filename, content);
            index.addAdd(newBlob);
        } else {
            Blob currentBlob = Blob.fromFile(currentId);
            Blob mergeBlob = Blob.fromFile(mergeId);
            String currentContent = currentBlob.getContent();
            String mergeContent = mergeBlob.getContent();
            File file = new File(CWD, filename);
            String content = "<<<<<<< HEAD" + "\n" + currentContent + "=======\n"
                    + mergeContent + ">>>>>>>" + "\n";
            writeContents(file, content);
            Blob newBlob = new Blob(filename, content);
            index.addAdd(newBlob);
        }
        index.saveFile();
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
        Map<String, String> allBlobs = new HashMap<>(currentBlobs);
        allBlobs.putAll(mergeBlobs);
        allBlobs.putAll(mutualBlobs);
        return allBlobs;
    }

    /**
     * As a helper method to check files only tracked by merge commits.
     *
     * @param currentCommit the current commit.
     * @param mergeCommit   the merge commit.
     */
    private static void checkFiles(Commit currentCommit, Commit mergeCommit) {
        // If the merge overwrites or deletes an untracked file in the current commit
        List<String> workingFiles = plainFilenamesIn(CWD);
        if (workingFiles != null && !workingFiles.isEmpty()) {
            for (String filename : workingFiles) {
                if (mergeCommit.containsFile(filename) && !currentCommit.containsFile(filename)) {
                    // If the file is modified in the current commit,
                    message("There is an untracked file in the way; delete it, "
                            + "or add and commit it first.");
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
                    } else if (!visited1.contains(parent)) {
                        // Otherwise continue traversing
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

    /**
     * Saves the given login information under the given remote name.
     *
     * @param name          the name of the remote.
     * @param directoryPath the directory path of the remote.
     */
    public static void addRemote(String name, String directoryPath) {
        File remoteFile = new File(REMOTE_DIR, name);
        if (remoteFile.exists()) {
            message("A remote with that name already exists.");
            System.exit(0);
        } else {
            try {
                remoteFile.createNewFile();
                String path = directoryPath.replace("/", java.io.File.separator);
                writeContents(remoteFile, path);
            } catch (IOException e) {
                message("Error: Could not add remote.");
            }
        }
    }

    /**
     * Remove information associated with the given remote name.
     *
     * @param name the name of the remote to remove.
     */
    public static void rmRemote(String name) {
        File remoteFile = new File(REMOTE_DIR, name);
        if (remoteFile.exists()) {
            remoteFile.delete();
        } else {
            message("A remote with that name does not exist.");
            System.exit(0);
        }
    }

    /**
     * Attempts to append the current branch’s commits to the end of the given branch at the given remote
     *
     * @param remoteName       the name of the remote.
     * @param remoteBranchName the name of the remote branch.
     */
    public static void push(String remoteName, String remoteBranchName) {
        // Check if the remote exists
        String remotePath = readContentsAsString(new File(REMOTE_DIR, remoteName));
        File remoteGitletDir = join(remotePath);
        if (!remoteGitletDir.exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }
        File remoteBranchFile = new File(remoteGitletDir, "refs/heads/" + remoteBranchName);
        if (!remoteBranchFile.exists()) {
            try {
                remoteBranchFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error: Could not create remote branch file.");
            }
        }
        String remoteCommitId = readContentsAsString(remoteBranchFile);
        List<String> commitHistory = plainFilenamesIn(Commit.COMMITS_DIR);
        if (commitHistory != null && !commitHistory.contains(remoteCommitId)) {
            message("Please pull down remote changes before pushing.");
            System.exit(0);
        }
        String currentBranch = readContentsAsString(HEAD_FILE);
        Commit currentCommit = readObject(new File(Commit.COMMITS_DIR,
                readContentsAsString(new File(BRANCH_DIR, currentBranch))), Commit.class);
        File remoteCommitDir = join(remoteGitletDir, "objects", "commits");
        File remoteCommitFile = new File(remoteCommitDir, currentCommit.getId());
        writeObject(remoteCommitFile, currentCommit);
        List<String> listOfParents = currentCommit.getParents();
        while (!listOfParents.isEmpty()) {
            Commit parentCommit = readObject(new File(Commit.COMMITS_DIR, listOfParents.get(0)), Commit.class);
            if (parentCommit.getId().equals(remoteCommitId)) {
                break;
            }
            File parentCommitFile = new File(remoteCommitDir, parentCommit.getId());
            writeObject(parentCommitFile, parentCommit);
            listOfParents = parentCommit.getParents();
        }
        writeContents(remoteBranchFile, currentCommit.getId());
    }

    /**
     * Brings down commits from the remote Gitlet repository into the local Gitlet repository.
     *
     * @param remoteName       the name of the remote.
     * @param remoteBranchName the name of the remote branch.
     */
    public static void fetch(String remoteName, String remoteBranchName) {
        // Check if the remote exists
        String remotePath = readContentsAsString(new File(REMOTE_DIR, remoteName));
        File remoteGitletDir = join(remotePath);
        if (!remoteGitletDir.exists()) {
            message("Remote directory not found.");
            System.exit(0);
        }
        File remoteBranchFile = new File(remoteGitletDir, "refs/heads/" + remoteBranchName);
        if (!remoteBranchFile.exists()) {
            message("That remote does not have that branch.");
            System.exit(0);
        }
        // because windows can't create a file with a colon in the name,
        // we use the remote name and branch name as the branch name,
        // so we also need to modify in the checkout method
        String newBranchName = remoteName + remoteBranchName;
        File fetchBranchFile = new File(BRANCH_DIR, newBranchName);
        if (!fetchBranchFile.exists()) {
            try {
                fetchBranchFile.createNewFile();
            } catch (IOException e) {
                System.err.println("Error: Could not create branch file.");
            }
        }
        List<String> allCommits = plainFilenamesIn(Commit.COMMITS_DIR);
        List<String> allBlobs = plainFilenamesIn(Blob.BLOBS_DIR);
        File remoteCommitDir = join(remoteGitletDir, "objects", "commits");
        File remoteBlobDir = join(remoteGitletDir, "objects", "blobs");
        Commit remoteCommit = readObject(new File(remoteCommitDir,
                readContentsAsString(remoteBranchFile)), Commit.class);
        // first, write the remote commit and its corresponding blobs to the local repository
        // if it doesn't already exist
        writeCommitsAndBlobs(allCommits, allBlobs, remoteBlobDir, remoteCommit);
        List<String> listOfParents = remoteCommit.getParents();
        while (listOfParents != null && !listOfParents.isEmpty()) {
            Commit parentCommit = readObject(new File(Commit.COMMITS_DIR, listOfParents.get(0)), Commit.class);
            writeCommitsAndBlobs(allCommits, allBlobs, remoteBlobDir, remoteCommit);
            listOfParents = parentCommit.getParents();
        }
        writeContents(fetchBranchFile, remoteCommit.getId());
    }

    /**
     * Helper method to write commits and blobs to the local repository.
     *
     * @param allCommits    the list of all commit ids in the local repository.
     * @param allBlobs      the list of all blob ids in the local repository.
     * @param remoteBlobDir the directory of the remote blobs.
     * @param remoteCommit  the remote commit to write.
     */
    private static void writeCommitsAndBlobs(List<String> allCommits, List<String> allBlobs,
                                             File remoteBlobDir, Commit remoteCommit) {
        if (allCommits != null && !allCommits.contains(remoteCommit.getId())) {
            File commitFile = new File(Commit.COMMITS_DIR, remoteCommit.getId());
            writeObject(commitFile, remoteCommit);
        }
        for (Map.Entry<String, String> entry : remoteCommit.getBlobMap().entrySet()) {
            Blob blob = readObject(new File(remoteBlobDir, entry.getValue()), Blob.class);
            if (allBlobs != null && !allBlobs.contains(entry.getValue())) {
                File blobFile = new File(Blob.BLOBS_DIR, entry.getValue());
                writeObject(blobFile, blob);
            }
        }
    }

    /**
     * Fetches branch [remote name]/[remote branch name] as for the fetch command,
     * and then merges that fetch into the current branch.
     *
     * @param remoteName       the name of the remote.
     * @param remoteBranchName the name of the remote branch.
     */
    public static void pull(String remoteName, String remoteBranchName) {
        fetch(remoteName, remoteBranchName);
        merge(remoteName + remoteBranchName);
    }
}


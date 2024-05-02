package gitlet;

import java.io.File;

import static gitlet.Utils.*;

/**
 * Driver class for Gitlet, a subset of the Git version-control system.
 *
 * @author R7CKB
 */
public class Main {
    /**
     * Usage: java gitlet.Main ARGS, where ARGS addContainsId
     * <COMMAND> <OPERAND1> <OPERAND2> ...
     * Because for the style check, we need to separate each command into a separate method,
     * we've separated the code into different methods for each command.
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                init(args);
                break;
            case "add":
                add(args);
                break;
            case "commit":
                commit(args);
                break;
            case "rm":
                rm(args);
                break;
            case "log":
                log(args);
                break;
            case "global-log":
                globalLog(args);
                break;
            case "find":
                find(args);
                break;
            case "status":
                status(args);
                break;
            case "checkout":
                checkout(args);
                break;
            case "branch":
                branch(args);
                break;
            case "rm-branch":
                rmBranch(args);
                break;
            case "reset":
                reset(args);
                break;
            case "merge":
                merge(args);
                break;
            case "add-remote":
                addRemote(args);
                break;
            case "rm-remote":
                rmRemote(args);
                break;
            case "push":
                push(args);
                break;
            case "fetch":
                fetch(args);
                break;
            case "pull":
                pull(args);
                break;
            default:
                // not precise enough, but it's a start
                // firstly I think it not precise enough, but it's enough.
                message("No command with that name exists.");
                System.exit(0);
        }
    }

    /**
     * Handles the init command.
     *
     * @param args The command line arguments for the init command
     */
    private static void init(String[] args) {
        validateNumArgsAndFormat("init", args, 1);
        Repository.init();
    }

    /**
     * Handles the add command.
     *
     * @param args The command line arguments for the add command
     */
    private static void add(String[] args) {
        validateNumArgsAndFormat("add", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String filename = args[1];   // the second argument is the filename
        Repository.add(filename);
    }

    /**
     * Handles the commit command.
     *
     * @param args The command line arguments for the commit command
     */
    private static void commit(String[] args) {
        validateNumArgsAndFormat("commit", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String message = args[1];   // the second argument is the message
        Repository.commit(message);
    }

    /**
     * Handles the rm command.
     *
     * @param args The command line arguments for the rm command
     */
    private static void rm(String[] args) {
        validateNumArgsAndFormat("rm", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String rmFilename = args[1];   // the second argument is the filename
        Repository.rm(rmFilename);
    }

    /**
     * Handles the log command.
     *
     * @param args The command line arguments for the log command
     */
    private static void log(String[] args) {
        validateNumArgsAndFormat("log", args, 1);
        validateGitletDirectory(new File(".gitlet"));
        Repository.log();
    }

    /**
     * Handles the global-log command.
     *
     * @param args The command line arguments for the global-log command
     */
    private static void globalLog(String[] args) {
        validateNumArgsAndFormat("global-log", args, 1);
        validateGitletDirectory(new File(".gitlet"));
        Repository.globalLog();
    }

    /**
     * Handles the find command.
     *
     * @param args The command line arguments for the find command
     */
    private static void find(String[] args) {
        validateNumArgsAndFormat("find", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String commitMessage = args[1];   // the second argument is the commit message
        Repository.find(commitMessage);
    }

    /**
     * Handles the status command.
     *
     * @param args The command line arguments for the status command
     */
    private static void status(String[] args) {
        validateNumArgsAndFormat("status", args, 1);
        validateGitletDirectory(new File(".gitlet"));
        Repository.status();
    }

    /**
     * Handles the branch command.
     *
     * @param args The command line arguments for the branch command
     */
    private static void branch(String[] args) {
        validateNumArgsAndFormat("branch", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String branchName = args[1];   // the second argument is the branch name
        Repository.branch(branchName);
    }

    /**
     * Handles the rm-branch command.
     *
     * @param args The command line arguments for the rm-branch command
     */
    private static void rmBranch(String[] args) {
        validateNumArgsAndFormat("rm-branch", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String rmBranchName = args[1];   // the second argument is the branch name
        Repository.rmBranch(rmBranchName);
    }

    /**
     * Handles the reset command.
     *
     * @param args The command line arguments for the reset command
     */
    private static void reset(String[] args) {
        validateNumArgsAndFormat("reset", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String commitId = args[1];   // the second argument is the commit id
        Repository.reset(commitId);
    }

    /**
     * Handles the merge command.
     *
     * @param args The command line arguments for the merge command
     */
    private static void merge(String[] args) {
        validateNumArgsAndFormat("merge", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String mergeBranch = args[1];   // the second argument is the branch name
        Repository.merge(mergeBranch);
    }

    /**
     * Handles the add-remote command.
     *
     * @param args The command line arguments for the add-remote command
     */
    private static void addRemote(String[] args) {
        validateNumArgsAndFormat("add-remote", args, 3);
        validateGitletDirectory(new File(".gitlet"));
        String remoteName = args[1];   // the second argument is the remote name
        String remoteDirectory = args[2];   // the third argument is the remote URL
        Repository.addRemote(remoteName, remoteDirectory);
    }

    /**
     * Handles the rm-remote command.
     *
     * @param args The command line arguments for the rm-remote command
     */
    private static void rmRemote(String[] args) {
        validateNumArgsAndFormat("rm-remote", args, 2);
        validateGitletDirectory(new File(".gitlet"));
        String remoteNameToRemove = args[1];   // the second argument is the remote name
        Repository.rmRemote(remoteNameToRemove);
    }

    /**
     * Handles the push command.
     *
     * @param args The command line arguments for the push command
     */
    private static void push(String[] args) {
        validateNumArgsAndFormat("push", args, 3);
        validateGitletDirectory(new File(".gitlet"));
        String remoteNameToPush = args[1];   // the second argument is the remote name
        String branchToPush = args[2];   // the third argument is the branch name
        Repository.push(remoteNameToPush, branchToPush);
    }

    /**
     * Handles the fetch command.
     *
     * @param args The command line arguments for the fetch command
     */
    private static void fetch(String[] args) {
        validateNumArgsAndFormat("fetch", args, 3);
        validateGitletDirectory(new File(".gitlet"));
        String remoteNameToFetch = args[1];   // the second argument is the remote name
        String branchToFetch = args[2];   // the third argument is the branch name
        Repository.fetch(remoteNameToFetch, branchToFetch);
    }

    /**
     * Handles the pull command.
     *
     * @param args The command line arguments for the pull command
     */
    private static void pull(String[] args) {
        validateNumArgsAndFormat("pull", args, 3);
        validateGitletDirectory(new File(".gitlet"));
        String remoteNameToPull = args[1];   // the second argument is the remote name
        String branchToPull = args[2];   // the third argument is the branch name
        Repository.pull(remoteNameToPull, branchToPull);
    }


    /**
     * Handles the checkout command.
     *
     * @param args The command line arguments for the checkout command
     */
    private static void checkout(String[] args) {
        if (args.length == 2) {
            validateNumArgsAndFormat("checkout", args, 2);
            validateGitletDirectory(new File(".gitlet"));
            String branch = args[1];
            Repository.checkout(branch, true);
        } else if (args.length == 3) {
            validateNumArgsAndFormat("checkout", args, 3);
            validateGitletDirectory(new File(".gitlet"));
            String checkoutFilename = args[2];
            Repository.checkout(checkoutFilename, false);
        } else if (args.length == 4) {
            validateNumArgsAndFormat("checkout", args, 4);
            validateGitletDirectory(new File(".gitlet"));
            String commitId = args[1];
            String fileName = args[3];
            Repository.checkout(commitId, fileName);
        }
    }

    /**
     * Checks the number of arguments versus the expected number,
     * if the number is correct, validates the format of the operands.
     * If not, print an error message and exits.
     *
     * @param cmd  Name of command you're validating
     * @param args Argument array from command line
     * @param n    Number of expected arguments
     */
    public static void validateNumArgsAndFormat(String cmd, String[] args, int n) {
        // validate the format of the operands
        if (cmd.equals("commit")) {
            if (args[1].isEmpty()) {
                message("Please enter a commit message.");
                System.exit(0);
            }
        }
        if (cmd.equals("checkout") && n == 4) {
            if (!args[2].equals("--")) {
                message("Incorrect operands.");
                System.exit(0);
            }
        }
        if (args.length != n) {
            message("Incorrect operands.");
            System.exit(0);
        }
    }

    /**
     * Checks if the current directory is a Gitlet directory.
     * If not, print an error message and exits.
     *
     * @param gitletDir The Gitlet directory to check
     */
    public static void validateGitletDirectory(File gitletDir) {
        if (!gitletDir.exists()) {
            message("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }
}

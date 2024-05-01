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
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            message("Please enter a command.");
            System.exit(0);
        }
        String firstArg = args[0];
        switch (firstArg) {
            case "init":
                validateNumArgsAndFormat("init", args, 1);
                Repository.init();
                break;
            case "add":
                validateNumArgsAndFormat("add", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String filename = args[1];   // the second argument is the filename
                Repository.add(filename);
                break;
            case "commit":
                validateNumArgsAndFormat("commit", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String message = args[1];   // the second argument is the message
                Repository.commit(message);
                break;
            case "rm":
                validateNumArgsAndFormat("rm", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String rmFilename = args[1];   // the second argument is the filename
                Repository.rm(rmFilename);
                break;
            case "log":
                validateNumArgsAndFormat("log", args, 1);
                validateGitletDirectory(new File(".gitlet"));
                Repository.log();
                break;
            case "global-log":
                validateNumArgsAndFormat("global-log", args, 1);
                validateGitletDirectory(new File(".gitlet"));
                Repository.globalLog();
                break;
            case "find":
                validateNumArgsAndFormat("find", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String commitMessage = args[1];   // the second argument is the commit message
                Repository.find(commitMessage);
                break;
            case "status":
                validateNumArgsAndFormat("status", args, 1);
                validateGitletDirectory(new File(".gitlet"));
                Repository.status();
                break;
            case "checkout":
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
                break;
            case "branch":
                validateNumArgsAndFormat("branch", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String branchName = args[1];   // the second argument is the branch name
                Repository.branch(branchName);
                break;
            case "rm-branch":
                validateNumArgsAndFormat("rm-branch", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String rmBranchName = args[1];   // the second argument is the branch name
                Repository.rmBranch(rmBranchName);
                break;
            case "reset":
                validateNumArgsAndFormat("reset", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String CommitId = args[1];   // the second argument is the commit id
                Repository.reset(CommitId);
                break;
            case "merge":
                validateNumArgsAndFormat("merge", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String mergeBranch = args[1];   // the second argument is the branch name
                Repository.merge(mergeBranch);
                break;
            case "add-remote":
                validateNumArgsAndFormat("add-remote", args, 3);
                validateGitletDirectory(new File(".gitlet"));
                String remoteName = args[1];   // the second argument is the remote name
                String remoteDirectory = args[2];   // the third argument is the remote URL
                Repository.addRemote(remoteName, remoteDirectory);
                break;
            case "rm-remote":
                validateNumArgsAndFormat("rm-remote", args, 2);
                validateGitletDirectory(new File(".gitlet"));
                String remoteNameToRemove = args[1];   // the second argument is the remote name
                Repository.rmRemote(remoteNameToRemove);
                break;
            case "push":
                validateNumArgsAndFormat("push", args, 3);
                validateGitletDirectory(new File(".gitlet"));
                String remoteNameToPush = args[1];   // the second argument is the remote name
                String branchToPush = args[2];   // the third argument is the branch name
                Repository.push(remoteNameToPush, branchToPush);
                break;
            case "fetch":
                validateNumArgsAndFormat("fetch", args, 3);
                validateGitletDirectory(new File(".gitlet"));
                String remoteNameToFetch = args[1];   // the second argument is the remote name
                String branchToFetch = args[2];   // the third argument is the branch name
                Repository.fetch(remoteNameToFetch, branchToFetch);
                break;
            case "pull":
                validateNumArgsAndFormat("pull", args, 3);
                validateGitletDirectory(new File(".gitlet"));
                String remoteNameToPull = args[1];   // the second argument is the remote name
                String branchToPull = args[2];   // the third argument is the branch name
                Repository.pull(remoteNameToPull, branchToPull);
                break;
            default:
                // not precise enough, but it's a start
                message("No command with that name exists.");
                System.exit(0);
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
        // TODO: modify this method into a boolean method?
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

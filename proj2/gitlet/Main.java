package gitlet;

import static gitlet.Utils.error;
import static gitlet.Utils.message;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author Tsz Chun Chui
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        /** what if args is empty? */
        if (args.length == 0) {
            System.out.println("Please enter a command:");
            System.exit(0);
        } else if (!Repository.GITLET_DIR.exists() && !args[0].equals("init")) {
            System.out.println("Not in an initialized Gitlet directory");
        } else {
            String firstArg = args[0];
            switch(firstArg) {
                case "init":
                    if (args.length != 1) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.initgitlet();
                    break;
                case "add":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.add(args[1]);
                    break;
                case "commit":
                    if (args.length < 2 || args[1].length() < 1) {
                        System.out.println("Please enter a commit message.");
                        break;
                    }
                    Repository.commit(args[1], null);
                    break;
                case "rm":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.remove(args[1]);
                    break;
                case "log":
                    Repository.printlog();
                    break;
                case "global-log":
                    Repository.printgloballog();
                    break;
                case "checkout":
                    if (args.length == 3 && args[1].equals("--")) {
                        Repository.checkoutheadcommit(args[2]);
                        break;
                    } else if (args.length == 4 && args[2].equals("--")) {
                        Repository.checkoutpastcommit(args[1], args[3]);
                        break;
                    } else if (args.length == 2) {
                        Repository.checkoutbranch(args[1]);
                        break;
                    } else {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                case "branch":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.branch(args[1]);
                    break;
                case "find":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.find(args[1]);
                    break;
                case "rm-branch":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.rmbranch(args[1]);
                    break;
                case "status":
                    Repository.status();
                    break;
                case "reset":
                    Repository.reset(args[1]);
                    break;
                case "merge":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands.");
                        break;
                    }
                    Repository.mergeCheck(args[1]);
                    break;
                default:
                    System.out.println("No command with that name exists.");
                    System.exit(0);
            }
        }
    }
}

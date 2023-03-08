package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Invalid number of arguments");
        }
        else {
            String firstArg = args[0];
            switch(firstArg) {
                case "init":
                    Repository.initgitlet();
                    break;
                case "add":
                    Repository.add(args[1]);
                    break;
                case "commit":
                    if (args.length < 2 || args[1].length() < 1) {
                        System.out.println("Please enter a commit message.");
                        break;
                    }
                    Repository.commit(args[1]);
                    break;
                case "rm":
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
                        System.out.println("Incorrect operands");
                        break;
                    }
                case "branch":
                    Repository.branch(args[1]);
                    break;
                case "find":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands");
                        break;
                    }
                    Repository.find(args[1]);
                    break;
                case "rm-branch":
                    if (args.length != 2) {
                        System.out.println("Incorrect operands");
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
            }
        }
    }
}

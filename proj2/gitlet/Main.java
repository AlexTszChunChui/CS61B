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
                // TODO: FILL THE REST IN
                case "commit":
                    if (args.length < 2) {
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
            }
        }
    }
}

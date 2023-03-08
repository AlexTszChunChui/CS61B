package gitlet;

import jdk.jshell.execution.Util;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository  {

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory stored a Hashmap of UID and Commit. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "Commits");
    /** A file stored UID of a master Branch Object */
    public static File Branch = join(GITLET_DIR, "Branch");
    /** A file stored current Branch name */
    public static File Head = join(GITLET_DIR, "head");
    /** A folder stored all the snapshot of file */
    public static File blops = join(GITLET_DIR, "blops");
    /** A hashmap that keep track on staged file */
    public static File StagingArea = join(blops, "StagingArea");
    /** A hashset stored all Commit Object UID */
    public static File CommitSet = join(COMMIT_DIR, "CommitSet");

    public static void initgitlet() {
        /** create all the folder and file. */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            return;
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        Branch.mkdir();
        blops.mkdir();
        createfile(Head);
        createfile(join(Branch, "master"));
        createfile(StagingArea);
        writeObject(StagingArea, new HashMap<String, String>());
        writeObject(CommitSet, new HashSet<>());

        /** create the initial commit. */
        Commit initialcommit =
                new Commit("initial commit", null, new Date(0), new HashMap<String, String>());
        String UID = initialcommit.getUID();
        writeContents(Head, "master");
        writeContents(Headfile(), UID);
    }

    public static void add(String name) {
        /** check if the file need to be added exists */
        File staging = new File (CWD, name);
        if (!staging.exists()) {
            System.out.println("File does not exist.");
            return;
        }
        /** adding added file to blops and StagingMap */
        HashMap StagingMap = stagingarea();
        HashMap tracked = headcommit().gettracker();
        byte[] contents = readContents(staging);
        String UID = Utils.sha1(contents);
        File staged = new File (blops, UID);

        if (staged.exists() && tracked.containsKey(name)) {
            StagingMap.remove(name);
        } else if (staged.exists()) {
            StagingMap.put(name, UID);
        } else {
            createfile(staged);
            writeContents(staged, contents);
            StagingMap.put(name, UID);
        }
        writeObject(StagingArea, StagingMap);
    }

    public static void commit(String message) {
        /** Check if there are any changes to commit */
        HashMap<String, String> StagingMap = stagingarea();
        if (StagingMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        /** createing new Commit Object instance variable */
        Commit parent = headcommit();
        HashMap tracker = parent.gettracker();

        /** copy staged history from current commit and update it base on Staging Area */
        for (Map.Entry<String, String> entry : StagingMap.entrySet()) {
            String filename = entry.getKey();
            String UID = entry.getValue();
            if (UID == null) {
                tracker.remove(filename);
            }else {
                tracker.put(filename, UID);
            }
        }
        Commit commit = new Commit(message, parent.getUID(), new Date(), tracker);

        /** updating all the information */
        String UID = commit.getUID();
        writeContents(Headfile(), UID);
        writeObject(StagingArea, new HashMap<String, String>());
    }

    public static void remove(String name) {
        HashMap<String, String> StagingMap = stagingarea();
        HashMap tracked = headcommit().gettracker();
        File rm = new File(CWD, name);

        if (StagingMap.containsKey(name)) {
            String UID = StagingMap.get(name);
            File staged = new File(blops, UID);
            staged.delete();
            StagingMap.remove(name);
            writeObject(StagingArea, StagingMap);
        } else if (tracked.containsKey(name)) {
            StagingMap.put(name, null);
            writeObject(StagingArea, StagingMap);
            Utils.restrictedDelete(rm);
        } else {
            System.out.println("No reason to remove the file");
        }
    }

    /** date must be in Wed Dec 31 16:00:00 1969 -0800 format */
    public static void printlog() {
        Commit head = headcommit();
        while (head != null) {
            System.out.println("===");
            System.out.println("commit " + head.getUID());
            System.out.println("Date: " + head.getTimestamp());
            System.out.println(head.getMessage());
            System.out.println("");
            head = head.getParent();
        }
    }

    public static void printgloballog() {
        HashSet<String> UIDSet = readObject(CommitSet, HashSet.class);
        for (String UID : UIDSet) {
            Commit c = Commit.getCommit(UID);
            System.out.println("===");
            System.out.println("commit " + c.getUID());
            System.out.println("Date: " + c.getTimestamp());
            System.out.println(c.getMessage());
            System.out.println("");
        }
    }

    public static void status() {
        System.out.println("=== Branches ===");
        printbranch();
        System.out.println("");
        System.out.println("=== Staged Files ===");
        printstaged();
        System.out.println("");
        System.out.println("=== Removed Files ===");
        printremoved();
        System.out.println("");
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println("");
        System.out.println("=== Untracked Files ===");
        System.out.println("");
    }

    private static void printbranch() {
        List<String> lst = plainFilenamesIn(Branch);
        Collections.sort(lst);
        for (String name : lst) {
            if (iscurrentbranch(join(Branch, name))) {
                System.out.println("*" + name);
            } else {
                System.out.println(name);
            }
        }
    }

    private static void printstaged() {
        Map staged = stagingarea();
        if (staged.isEmpty()) {
            return;
        }
        SortedSet<String> filename = new TreeSet<>(staged.keySet());
        for (String name : filename) {
            if (staged.get(name) != null) {
                System.out.println(name);
            }
        }
    }

    private static void printremoved() {
        Map staged = stagingarea();
        SortedSet<String> filename = new TreeSet<>(staged.keySet());
        for (String name : filename) {
            if (staged.get(name) == null) {
                System.out.println(name);
            }
        }
    }

    public static void find(String message) {
        boolean exist = false;
        HashSet<String> UIDSet = readObject(CommitSet, HashSet.class);
        for (String UID : UIDSet) {
            Commit c = Commit.getCommit(UID);
            if (c.getMessage().equals(message)) {
                exist = true;
                System.out.println(c.getUID());
            }
        }
        if (!exist) {
            System.out.println("Found no commit with that message.");
        }
    }

    public static void checkoutheadcommit(String name) {
        HashSet<String> lst = new HashSet<>();
        lst.add(name);
        checkout(headcommit(), lst);
    }

    public static void checkoutpastcommit(String UID, String name) {
        if (UID.length() < 6 && UID.length() > 40) {
            System.out.println("Incorrect operands");
            return;
        }
        Commit pastcommit = Commit.getCommit(UID);
        if (pastcommit == null) {
            System.out.println("No commit with that id exists");
            return;
        }
        HashSet<String> lst = new HashSet<>();
        lst.add(name);
        checkout(pastcommit, lst);
    }

    public static void checkoutbranch(String name) {
        File newbranch = Utils.join(Branch, name);
        if (!newbranch.exists()) {
            System.out.println("No such branch exists");
        } else if (iscurrentbranch(newbranch)) {
            System.out.println("No need to checkout the current branch.");
        /** passing the relative commit into untrackedfile helper function */
        } else if (untrackedfile(Commit.getCommit(readContentsAsString(newbranch)))) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        } else {
            clearup(Commit.getCommit(readContentsAsString(newbranch)));
            writeContents(Head, name);
            Commit c = headcommit();
            checkout(c, c.gettracker().keySet());
        }
    }

    private static void checkout(Commit location, Set<String> set) {
        /** Finding the relative File of the commit */
        Map tracker = location.gettracker();
        for (String name : set) {
            String FileUID = (String) tracker.get(name);
            if (FileUID == null) {
                System.out.println("File does not exist in that commit.");
                return;
            }

            /** checkout the file user required */
            File blop = Utils.join(blops, FileUID);
            File oldversion = Utils.join(CWD, name);
            byte[] contents = readContents(blop);
            if (!oldversion.exists()) {
                createfile(oldversion);
            }
            writeContents(oldversion, contents);
        }
        /** clearup the stagingarea */
        writeObject(StagingArea, new HashMap<String, String>());
    }

    /** create branch file store respective Head Commit UID */
    public static void branch(String name){
        File newbranch = Utils.join(Branch, name);
        if (newbranch.exists()) {
            System.out.println("A branch with that name already exists");
            return;
        }
        createfile(newbranch);
        String CurrentHead = readContentsAsString(Headfile());
        writeContents(newbranch, CurrentHead);
    }

    /** delete the branch with the given name */
    public static void rmbranch(String name) {
        File rm = join(Branch, name);
        if (!rm.exists()) {
            System.out.println("A branch with that name does not exist");
        } else if (iscurrentbranch(rm)) {
            System.out.println("Cannot remove the current branch.");
        } else {
            rm.delete();
        }
    }

    /** checkout the specific commit */
    public static void reset(String commitid) {
        Commit pastcommit = Commit.getCommit(commitid);
        if (pastcommit == null) {
            System.out.println("No commit with that id exists");
            return;
        } else if (untrackedfile(pastcommit)) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
            return;
        }
        clearup(pastcommit);
        writeContents(Headfile(), commitid);
        checkout(pastcommit, pastcommit.gettracker().keySet());
    }

    /** shortcut for creating file */
    protected static void createfile(File file) {
        try {
            file.createNewFile();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** return the Commit Object that Head pointer pointing at */
    private static Commit headcommit() {
        String HeadUID = readContentsAsString(Headfile());
        return Commit.getCommit(HeadUID);
    }

    /** return the HashMap that stored the information of stagingarea */
    private static HashMap<String, String> stagingarea() {
        return readObject(StagingArea, HashMap.class);
    }

    /** helper function that check if the checkout branch is the currentbranch */
    private static boolean iscurrentbranch(File file) {
        try {
            if (file.getCanonicalPath().equals(Headfile().getCanonicalPath())) {
                return true;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    private static boolean untrackedfile(Commit branchhead) {
        Set<String> tracked = branchhead.gettracker().keySet();
        for (String name: tracked) {
            File file = new File (CWD, name);
            /** check if a file exists in CWD but neither staged for addition nor tracked */
            if (file.exists() && !stagingarea().containsKey(name) && !headcommit().gettracker().containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    /** helper function for delete file that are tracked in the current branch
     *  but are not present in the checked-out branch
     */
    private static void clearup(Commit branchhead) {
        Set<String> tracked = branchhead.gettracker().keySet();
        Set<String> currenttracked = headcommit().gettracker().keySet();
        for (String name : currenttracked) {
            if (!tracked.contains(name)) {
                File rm = join(CWD, name);
                restrictedDelete(rm);
            }
        }
    }

    /** return the headfile object in relative branch of current Head pointer point at */
    private static File Headfile() {
        return join(Branch, readContentsAsString(Head));
    }
}

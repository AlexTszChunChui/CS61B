package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static gitlet.Utils.*;


/** Represents a gitlet repository.
 *  Simplify a real git but with some function reduced
 *  @author Tsz Chun Chui
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
        String contents = readContentsAsString(staging);
        HashMap StagingMap = stagingarea();
        Map tracked = headcommit().gettracker();
        String UID = Utils.sha1(name + contents);
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
        Map tracker = parent.gettracker();

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
        List<String> parentUid = new ArrayList<>();
        parentUid.add(parent.getUID());
        Commit commit = new Commit(message, parentUid, new Date(), tracker);

        /** updating all the information */
        String UID = commit.getUID();
        writeContents(Headfile(), UID);
        writeObject(StagingArea, new HashMap<String, String>());
    }

    public static void remove(String name) {
        HashMap<String, String> StagingMap = stagingarea();
        Map tracked = headcommit().gettracker();
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

    /** performing check before any actions */
    public static void mergeCheck(String branchName) {
        File prevBranch = join(Branch, branchName);
        if (!stagingarea().isEmpty()) {
            System.out.println("You have uncommitted changes");
        } else if (!prevBranch.exists()) {
            System.out.println("A branch with that name does not exist.");
        } else if (iscurrentbranch(prevBranch)) {
            System.out.println("Cannot merge a branch with itself.");
        } else if (untrackedfile(Commit.getCommit(readContentsAsString(prevBranch)))) {
            System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
        } else {
            merge(branchName);
        }
    }
    /** merging two branches into one */
    private static void merge(String branchName) {
        String ancestorId = findAncestor(branchName);
        String branchId = readContentsAsString(join(Branch, branchName));

        if (ancestorId.equals(branchId)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
        } else if (ancestorId.equals(headcommit().getUID())) {
            checkoutbranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Commit other = Commit.getCommit(branchId);
        Commit ancestor = Commit.getCommit(ancestorId);

        Map headTracker = headcommit().gettracker();
        Map ancestorTracker = ancestor.gettracker();
        Map otherTracker = other.gettracker();

        boolean conflicted = false;
        for (Object fileName : otherTracker.keySet()) {
            /** file exists in all three Commit */
            if (ancestorTracker.containsKey(fileName) && headTracker.containsKey(fileName)) {
                String otherFile = (String) otherTracker.get(fileName);
                String ancestorFile = (String) ancestorTracker.get(fileName);
                String headFile = (String) headTracker.get(fileName);
                /** only the other branch modified the file */
                if (headFile.equals(ancestorFile) && !otherFile.equals(headFile)) {
                    checkoutpastcommit(branchId, (String) fileName);
                    stagingarea().put((String) fileName, otherFile);
                }
                /** both head & other modified the file */
                else if (!headFile.equals(ancestorFile) && !otherFile.equals(headFile)) {
                    conflict(headFile, otherFile, (String) fileName);
                    conflicted = true;
                }

            /** absent at the split point and exists in both head and other */
            } else if (!ancestorTracker.containsKey(fileName) && headTracker.containsKey(fileName)) {
                String headFile = (String) headTracker.get(fileName);
                String otherFile = (String) otherTracker.get(fileName);
                if (!headFile.equals(otherFile)) {
                    conflict(headFile, otherFile, (String) fileName);
                    conflicted = true;
                }

            /** only exists in other */
            } else if (!ancestorTracker.containsKey(fileName) && !headTracker.containsKey(fileName)) {
                String otherFile = (String) otherTracker.get(fileName);
                checkoutpastcommit(branchId, (String) fileName);
                stagingarea().put((String) fileName, otherFile);

            /** exists in other and ancestor but not in head */
            } else if (!headTracker.containsKey(fileName) && ancestorTracker.containsKey(fileName)) {
                String otherFile = (String) otherTracker.get(fileName);
                String ancestorFile = (String) ancestorTracker.get(fileName);
                if (!otherFile.equals(ancestorFile)) {
                    conflict(null, otherFile, (String) fileName);
                    conflicted = true;
                }
            }
        }
        /** searching for key only exists in headCommit */
        for (Object fileName : headTracker.keySet()) {
            if (ancestorTracker.containsKey(fileName) && !otherTracker.containsKey(fileName)) {
                String ancestorFile = (String) ancestorTracker.get(fileName);
                String headFile = (String) headTracker.get(fileName);
                if (ancestorFile.equals(headFile)) {
                    stagingarea().put((String) fileName, null);
                } else {
                    conflict(headFile, null, (String) fileName);
                    conflicted = true;
                }
            }
        }
        commit("Merged " + branchName + " into " + readContentsAsString(Head));
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }
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

    /** find the common ancestor commit UID of the given branch and current branch */
    private static String findAncestor(String branchName) {
        String branchId = readContentsAsString(join(Branch, branchName));
        Map<String, Integer> other = allAncestor(branchId);
        String headId = readContentsAsString(Headfile());
        Map<String, Integer> currentHead = allAncestor(headId);

        String closetId = null;
        int closetValue = Integer.MAX_VALUE;
        for (String id : currentHead.keySet()) {
            if (other.containsKey(id) && currentHead.get(id) < closetValue) {
                closetId = id;
                closetValue = currentHead.get(id);
            }
        }
        return closetId;
    }

    private static Map<String, Integer> allAncestor(String commitId) {
        Queue<String> pastCommit = new ArrayDeque<>();
        Map<String, Integer> pastUid = new TreeMap<>();
        pastCommit.offer(commitId);

        int depth = 1;
        while (!pastCommit.isEmpty()) {
            Commit c = Commit.getCommit(pastCommit.poll());
            pastUid.put(c.getUID(), depth);
            if (c.getParentList() == null) {
                break;
            }
            for (String id : c.getParentList()) {
                pastCommit.offer(id);
            }
            depth += 1;
        }
        return pastUid;
    }

    private static void conflict(String headFile, String otherFile, String fileName) {
        String headContent = "";
        String otherContent = "";

        if (headFile == null) {
            otherContent = readContentsAsString(join(blops, otherFile));
        } else if (otherFile == null) {
            headContent = readContentsAsString(join(blops, headFile));
        } else {
            otherContent = readContentsAsString(join(blops, otherFile));
            headContent = readContentsAsString(join(blops, headFile));
        }

        String contents = "<<<<<<< HEAD\n" + headContent + "=======\n" + otherContent + ">>>>>>>\n";
        File current = join(CWD, fileName);
        if (!current.exists()) {
            createfile(current);
        }
        writeContents(current, contents);
        add(fileName);
    }
    
}

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
    static File BRANCH = join(GITLET_DIR, "Branch");
    /** A file stored current Branch name */
    static File HEAD = join(GITLET_DIR, "head");
    /** A folder stored all the snapshot of file */
    static File BLOPS = join(GITLET_DIR, "blops");
    /** A hashmap that keep track on staged file */
    static File stagingArea = join(BLOPS, "StagingArea");
    /** A hashset stored all Commit Object UID */
    static File commitSet = join(COMMIT_DIR, "CommitSet");

    public static void initgitlet() {
        /** create all the folder and file. */
        if (GITLET_DIR.exists()) {
            exit("A Gitlet version-control system already exists in the current directory");
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        BRANCH.mkdir();
        BLOPS.mkdir();
        createfile(HEAD);
        createfile(join(BRANCH, "master"));
        createfile(stagingArea);
        writeObject(stagingArea, new HashMap<String, String>());
        writeObject(commitSet, new HashSet<>());

        /** create the initial commit. */
        Commit initialcommit =
                new Commit("initial commit", null, new Date(0), new HashMap<String, String>());
        String uid = initialcommit.getUID();
        writeContents(HEAD, "master");
        writeContents(headFile(), uid);
    }

    public static void add(String name) {
        /** check if the file need to be added exists */
        File staging = new File(CWD, name);
        if (!staging.exists()) {
            exit("File does not exist.");
        }

        /** adding added file to blops and StagingMap */
        String contents = readContentsAsString(staging);
        HashMap stagingMap = stagingarea();
        Map tracked = headcommit().gettracker();
        String uid = Utils.sha1(name + contents);
        File staged = new File(BLOPS, uid);

        if (staged.exists() && tracked.containsKey(name) && tracked.get(name).equals(uid)) {
            stagingMap.remove(name);
        } else if (staged.exists()) {
            stagingMap.put(name, uid);
        } else {
            createfile(staged);
            writeContents(staged, contents);
            stagingMap.put(name, uid);
        }
        writeObject(stagingArea, stagingMap);
    }

    public static void commit(String message, String secondParent) {
        /** Check if there are any changes to commit */
        Map<String, String> stagingMap = stagingarea();
        if (stagingMap.isEmpty()) {
            System.out.println("No changes added to the commit.");
            return;
        }
        /** createing new Commit Object instance variable */
        Commit parent = headcommit();
        Map tracker = parent.gettracker();

        /** copy staged history from current commit and update it base on Staging Area */
        for (Map.Entry<String, String> entry : stagingMap.entrySet()) {
            String filename = entry.getKey();
            String uid = entry.getValue();
            if (uid == null) {
                tracker.remove(filename);
            } else {
                tracker.put(filename, uid);
            }
        }
        List<String> parentUid = new ArrayList<>();
        parentUid.add(parent.getUID());
        if (secondParent != null) {
            parentUid.add(secondParent);
        }
        Commit commit = new Commit(message, parentUid, new Date(), tracker);

        /** updating all the information */
        String uid = commit.getUID();
        writeContents(headFile(), uid);
        writeObject(stagingArea, new HashMap<String, String>());
    }

    public static void remove(String name) {
        HashMap<String, String> stagingMap = stagingarea();
        Map tracked = headcommit().gettracker();
        File rm = new File(CWD, name);

        if (stagingMap.containsKey(name)) {
            String uid = stagingMap.get(name);
            File staged = new File(BLOPS, uid);
            staged.delete();
            stagingMap.remove(name);
            writeObject(stagingArea, stagingMap);
        } else if (tracked.containsKey(name)) {
            stagingMap.put(name, null);
            writeObject(stagingArea, stagingMap);
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
            List<String> parent = head.getParentList();
            if (parent != null && parent.size() > 1) {
                String parent1 = parent.get(0).substring(0, 7);
                String parent2 = parent.get(1).substring(0, 7);
                String message = String.format("Merge: %s %s", parent1, parent2);
                System.out.println(message);
            }
            System.out.println("Date: " + head.getTimestamp());
            System.out.println(head.getMessage());
            System.out.println("");
            head = head.getParent();
        }
    }

    public static void printgloballog() {
        HashSet<String> uidSet = readObject(commitSet, HashSet.class);
        for (String uid : uidSet) {
            Commit c = Commit.getCommit(uid);
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
        List<String> lst = plainFilenamesIn(BRANCH);
        if (lst == null) {
            return;
        }
        Collections.sort(lst);
        for (String name : lst) {
            if (iscurrentbranch(join(BRANCH, name))) {
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
        HashSet<String> uidSet = readObject(commitSet, HashSet.class);
        for (String uid : uidSet) {
            Commit c = Commit.getCommit(uid);
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

    public static void checkoutpastcommit(String uid, String name) {
        if (uid.length() < 6 && uid.length() > 40) {
            System.out.println("Incorrect operands");
            return;
        }
        Commit pastcommit = Commit.getCommit(uid);
        if (pastcommit == null) {
            System.out.println("No commit with that id exists");
            return;
        }
        HashSet<String> lst = new HashSet<>();
        lst.add(name);
        checkout(pastcommit, lst);
    }

    public static void checkoutbranch(String name) {
        File newbranch = Utils.join(BRANCH, name);
        if (!newbranch.exists()) {
            exit("No such branch exists");
        } else if (iscurrentbranch(newbranch)) {
            exit("No need to checkout the current branch.");
        /** passing the relative commit into untrackedfile helper function */
        } else if (untrackedfile(Commit.getCommit(readContentsAsString(newbranch)))) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        } else {
            clearup(Commit.getCommit(readContentsAsString(newbranch)));
            writeContents(HEAD, name);
            Commit c = headcommit();
            checkout(c, c.gettracker().keySet());
        }
    }

    private static void checkout(Commit location, Set<String> set) {
        /** Finding the relative File of the commit */
        Map tracker = location.gettracker();
        for (String name : set) {
            String fileUid = (String) tracker.get(name);
            if (fileUid == null) {
                System.out.println("File does not exist in that commit.");
                return;
            }

            /** checkout the file user required */
            File blop = Utils.join(BLOPS, fileUid);
            File oldversion = Utils.join(CWD, name);
            byte[] contents = readContents(blop);
            if (!oldversion.exists()) {
                createfile(oldversion);
            }
            writeContents(oldversion, contents);
        }
        /** clearup the stagingarea */
        writeObject(stagingArea, new HashMap<String, String>());
    }

    /** create branch file store respective Head Commit UID */
    public static void branch(String name) {
        File newbranch = Utils.join(BRANCH, name);
        if (newbranch.exists()) {
            System.out.println("A branch with that name already exists");
            return;
        }
        createfile(newbranch);
        String currentHead = readContentsAsString(headFile());
        writeContents(newbranch, currentHead);
    }

    /** delete the branch with the given name */
    public static void rmbranch(String name) {
        File rm = join(BRANCH, name);
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
            exit("No commit with that id exists");
        } else if (untrackedfile(pastcommit)) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        }
        clearup(pastcommit);
        writeContents(headFile(), commitid);
        checkout(pastcommit, pastcommit.gettracker().keySet());
    }

    /** performing check before any actions */
    public static void mergeCheck(String branchName) {
        File prevBranch = join(BRANCH, branchName);
        if (!stagingarea().isEmpty()) {
            exit("You have uncommitted changes");
        } else if (!prevBranch.exists()) {
            exit("A branch with that name does not exist.");
        } else if (iscurrentbranch(prevBranch)) {
            exit("Cannot merge a branch with itself.");
        } else if (untrackedfile(Commit.getCommit(readContentsAsString(prevBranch)))) {
            exit("There is an untracked file in the way; delete it, or add and commit it first.");
        } else {
            String ancestorId = findAncestor(branchName);
            String branchId = readContentsAsString(join(BRANCH, branchName));

            if (ancestorId.equals(branchId)) {
                exit("Given branch is an ancestor of the current branch.");
            } else if (ancestorId.equals(headcommit().getUID())) {
                checkoutbranch(branchName);
                exit("Current branch fast-forwarded.");
            } else {
                merge(branchId, ancestorId, branchName);
            }
        }
    }

    /** merging two branches into one */
    private static void merge(String branchId, String ancestorId, String branchName) {
        Commit other = Commit.getCommit(branchId);
        Commit ancestor = Commit.getCommit(ancestorId);

        Map hdTrack = headcommit().gettracker();
        Map splitTrack = ancestor.gettracker();
        Map otherTrack = other.gettracker();

        boolean conflicted = false;
        for (Object fName : otherTrack.keySet()) {

            /** file exists in all three Commit */
            if (splitTrack.containsKey(fName) && hdTrack.containsKey(fName)) {
                String otherFile = (String) otherTrack.get(fName);
                String ancestorFile = (String) splitTrack.get(fName);
                String headFile = (String) hdTrack.get(fName);
                /** only the other branch modified the file */
                if (headFile.equals(ancestorFile) && !otherFile.equals(headFile)) {
                    checkoutpastcommit(branchId, (String) fName);
                    stagingFile((String) fName, otherFile);
                } else if (!headFile.equals(ancestorFile) && !otherFile.equals(ancestorFile)) {
                    /** both head & other modified the file */
                    if (!headFile.equals(otherFile)) {
                        conflict(headFile, otherFile, (String) fName);
                        conflicted = true;
                    }
                }
            /** absent at the split point and exists in both head and other */
            } else if (!splitTrack.containsKey(fName) && hdTrack.containsKey(fName)) {
                String headFile = (String) hdTrack.get(fName);
                String otherFile = (String) otherTrack.get(fName);
                if (!headFile.equals(otherFile)) {
                    conflict(headFile, otherFile, (String) fName);
                    conflicted = true;
                }
            /** only exists in other */
            } else if (!splitTrack.containsKey(fName) && !hdTrack.containsKey(fName)) {
                String otherFile = (String) otherTrack.get(fName);
                checkoutpastcommit(branchId, (String) fName);
                stagingFile((String) fName, otherFile);
            /** exists in other and ancestor but not in head */
            } else if (!hdTrack.containsKey(fName) && splitTrack.containsKey(fName)) {
                String otherFile = (String) otherTrack.get(fName);
                String ancestorFile = (String) splitTrack.get(fName);
                if (!otherFile.equals(ancestorFile)) {
                    conflict(null, otherFile, (String) fName);
                    conflicted = true;
                }
            }
        }
        /** searching for key only exists in headCommit */
        for (Object fileName : hdTrack.keySet()) {
            if (splitTrack.containsKey(fileName) && !otherTrack.containsKey(fileName)) {
                String ancestorFile = (String) splitTrack.get(fileName);
                String headFile = (String) hdTrack.get(fileName);
                if (ancestorFile.equals(headFile)) {
                    remove((String) fileName);
                } else {
                    conflict(headFile, null, (String) fileName);
                    conflicted = true;
                }
            }
        }
        String currentBranch = readContentsAsString(HEAD);
        String message = String.format("Merged %s into %s.", branchName, currentBranch);
        commit(message, branchId);
        if (conflicted) {
            System.out.println("Encountered a merge conflict.");
        }
    }

    /** shortcut for creating file */
    private static void createfile(File file) {
        try {
            file.createNewFile();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /** return the Commit Object that Head pointer pointing at */
    private static Commit headcommit() {
        String headUid = readContentsAsString(headFile());
        return Commit.getCommit(headUid);
    }

    /** return the HashMap that stored the information of stagingarea */
    private static HashMap<String, String> stagingarea() {
        return readObject(stagingArea, HashMap.class);
    }

    /** helper function that check if the checkout branch is the currentbranch */
    private static boolean iscurrentbranch(File file) {
        try {
            if (file.getCanonicalPath().equals(headFile().getCanonicalPath())) {
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
            File file = new File(CWD, name);
            /** check if a file exists in CWD but neither staged for addition nor tracked */
            Map headTrack = headcommit().gettracker();
            Map stage = stagingarea();
            boolean untrack = !stage.containsKey(name) && !headTrack.containsKey(name);
            if (file.exists() && untrack) {
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
    private static File headFile() {
        return join(BRANCH, readContentsAsString(HEAD));
    }

    /** find the common ancestor commit UID of the given branch and current branch */
    private static String findAncestor(String branchName) {
        String branchId = readContentsAsString(join(BRANCH, branchName));
        Map<String, Integer> other = allAncestor(branchId);
        String headId = readContentsAsString(headFile());
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
            otherContent = readContentsAsString(join(BLOPS, otherFile));
        } else if (otherFile == null) {
            headContent = readContentsAsString(join(BLOPS, headFile));
        } else {
            otherContent = readContentsAsString(join(BLOPS, otherFile));
            headContent = readContentsAsString(join(BLOPS, headFile));
        }

        String contents = "<<<<<<< HEAD\n" + headContent + "=======\n" + otherContent + ">>>>>>>\n";
        File current = join(CWD, fileName);
        if (!current.exists()) {
            createfile(current);
        }
        writeContents(current, contents);
        add(fileName);
    }

    private static void stagingFile(String fileName, String uid) {
        HashMap stagingMap = stagingarea();
        stagingMap.put(fileName, uid);
        writeObject(stagingArea, stagingMap);
    }

}

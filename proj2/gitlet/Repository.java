package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository  {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    /** The commit directory stored a Hashmap of UID and Commit. */
    public static final File COMMIT_DIR = join(GITLET_DIR, "Commits");
    /** A file stored UID of Head Commit Object */
    public static File Head = join(GITLET_DIR, "Head");
    /** A file stored UID of a master Commit Object */
    public static File master = join(GITLET_DIR, "master");
    /** A folder stored all the snapshot of file */
    public static File blops = join(GITLET_DIR, "blops");
    /** A hashmap that keep track on staged file */
    public static File StagingArea = join(GITLET_DIR, "StagingArea");

    public static void initgitlet() {
        /** create all the folder and file. */
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory");
            return;
        }
        GITLET_DIR.mkdir();
        COMMIT_DIR.mkdir();
        blops.mkdir();
        createfile(Head);
        createfile(master);
        createfile(StagingArea);
        writeObject(StagingArea, new HashMap<String, String>());

        /** create the initial commit. */
        Commit initialcommit =
                new Commit("initial commit", null, new Date(0), new HashMap<String, String>());
        String UID = initialcommit.getUID();
        writeContents(Head, UID);
        writeContents(master, UID);
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
        byte[] contents = readContents(staging);
        String UID = Utils.sha1(contents);
        File staged = new File (blops, UID);

        if (staged.exists()) {
            StagingMap.put(name, UID);
        }
        else {
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
        writeContents(Head, UID);
        writeContents(master, UID);
        writeObject(StagingArea, new HashMap<String, String>());
    }

    public static void remove(String name) {
        HashMap<String, String> StagingMap = stagingarea();
        HashMap tracked = headcommit().gettracker();
        File rm = new File(CWD, name);

        if (StagingMap.containsKey(name)) {
            String UID = StagingMap.get(name);
            File staged = new File(blops, UID);
            Utils.restrictedDelete(staged);
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

    public static void checkoutheadcommit(String name) {
        checkout(headcommit(), name);
    }

    public static void checkoutpastcommit(String UID, String name) {
        Commit pastcommit = Commit.getCommit(UID);
        checkout(pastcommit, name);
    }

    private static void checkout(Commit location, String name) {
        /** Finding the relative File of the commit */
        Map tracker = location.gettracker();
        String FileUID = (String) tracker.get(name);
        if (FileUID == null) {
            System.out.println("File does not exist in that commit.");
            return;
        }

        /** checkout the file user required */
        File blop = Utils.join(blops, FileUID);
        File oldversion = Utils.join(CWD, name);
        byte[] contents = readContents(blop);
        writeContents(oldversion, contents);
    }

    /** shortcut for creating file */
    public static void createfile(File file) {
        try {
            file.createNewFile();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static Commit headcommit() {
        String HeadUID = readContentsAsString(Head);
        return Commit.getCommit(HeadUID);
    }

    private static HashMap<String, String> stagingarea() {
        return readObject(StagingArea, HashMap.class);
    }

}

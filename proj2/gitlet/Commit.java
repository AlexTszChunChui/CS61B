package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    public static final File COMMIT_DIR = Repository.COMMIT_DIR;
    private String message;
    private Date timestamp;
    public ArrayList<String> parent = new ArrayList<>();
    private HashMap filetracker;
    private String UID;
    /** Something that keeps track of what files this commit is tracking */

    public Commit(String usermessage, String parentUID, Date date, HashMap staged) {
        this.message = usermessage;
        this.parent.add(parentUID);
        this.timestamp = date;
        this.filetracker = staged;
        this.UID = Utils.sha1(message, parent.toString(), timestamp.toString(), filetracker.toString());
        this.storecommit();
    }

    public String getMessage() {
        return this.message;
    }

    public String getTimestamp() {
        return timestamp.toString();
    }

    public Commit getParent() {
        HashMap<String, Commit> Commitsmap = readObject(COMMIT_DIR, HashMap.class);
        String UID = this.parent.get(0);
        if (UID == null) {
            return null;
        } else {
            return Commitsmap.get(UID);
        }
    }

    public HashMap gettracker() {
        return this.filetracker;
    }

    public String getUID() {
        return this.UID;
    }

    /** reading from the commit file, store itself init */
    public void storecommit() {
        String UID = this.getUID();
        HashMap<String, Commit> Commitsmap = readObject(COMMIT_DIR, HashMap.class);
        Commitsmap.put(UID, this);
        writeObject(COMMIT_DIR, Commitsmap);
    }
}

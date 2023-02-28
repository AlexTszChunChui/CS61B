package gitlet;

// TODO: any imports you need here

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
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
        SimpleDateFormat dateForm = new SimpleDateFormat("E MMM d HH:mm:ss YYYY Z", Locale.US);
        return dateForm.format(this.timestamp);
    }

    public Commit getParent() {
        return getCommit(this.parent.get(0));
    }

    public static Commit getCommit(String UID) {
        if (UID == null) {
            return null;
        }
        else {
            File twodigits = Utils.join(COMMIT_DIR, UID.substring(0, 2));
            List<String> lst = Utils.plainFilenamesIn(twodigits);
            for (String name : lst) {
                String fewerchar = name.substring(0, UID.length() - 2);
                if (fewerchar.equals(UID.substring(2))) {
                    File commit = Utils.join(twodigits, name);
                    return readObject(commit, Commit.class);
                }
            }
            return null;
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
        File twodigits = Utils.join(COMMIT_DIR, UID.substring(0, 2));
        twodigits.mkdir();
        File rest = Utils.join(twodigits, UID.substring(2));
        writeObject(rest, this);
    }
}

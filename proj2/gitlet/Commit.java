package gitlet;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  does at a high level.
 *  @author Tsz Chun Chui
 */
public class Commit implements Serializable {

    /** The message of this Commit. */
    private static final File COMMIT_DIR = Repository.COMMIT_DIR;
    private String message;
    private Date timestamp;
    private List<String> parent;
    private Map filetracker;
    private String UID;
    private static final File COMMITSET = Repository.CommitSet;


    public Commit(String usermessage, List<String> parentUID, Date date, Map staged) {
        this.message = usermessage;
        this.parent = parentUID;
        this.timestamp = date;
        this.filetracker = staged;
        this.UID = Utils.sha1(message, timestamp.toString(), filetracker.toString());
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
        if (this.parent == null) {
            return null;
        }
        return getCommit(this.parent.get(0));
    }

    public List<String> getParentList() {
        return parent;
    }

    public static Commit getCommit(String UID) {
        if (UID == null) {
            return null;
        }
        else {
            File twodigits = Utils.join(COMMIT_DIR, UID.substring(0, 2));
            List<String> lst = Utils.plainFilenamesIn(twodigits);
            if (lst == null) {
                return null;
            }
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

    public Map gettracker() {
        return this.filetracker;
    }

    public String getUID() {
        return this.UID;
    }

    /** reading from the commit file, store itself init */
    public void storecommit() {
        /** store this Commit Object in Commit folder */
        String UID = this.getUID();
        File twodigits = Utils.join(COMMIT_DIR, UID.substring(0, 2));
        twodigits.mkdir();
        File rest = Utils.join(twodigits, UID.substring(2));
        writeObject(rest, this);

        /** store the Commit UID in a Set Object */
        HashSet<String> UIDSet = readObject(COMMITSET, HashSet.class);
        UIDSet.add(UID);
        writeObject(COMMITSET, UIDSet);
    }
}

package byow.Core;

import java.io.*;

public class FileManagement {
    public static void writeObject(File file, Object content) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
            out.writeObject(content);
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object readObject(File file) {
        Object m;
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(file));
            m = inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return m;
    }
}

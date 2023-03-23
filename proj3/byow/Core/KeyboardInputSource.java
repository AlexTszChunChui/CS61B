package byow.Core;

import edu.princeton.cs.introcs.StdDraw;

public class KeyboardInputSource implements InputSource {

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                return c;
            }
            return '\0';
        }
    }

    public boolean possibleNextInput() {
        return true;
    }
}

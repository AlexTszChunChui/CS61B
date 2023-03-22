package byow.Core;

import java.awt.*;

public class Rect {
    int bottomLeftX;
    int bottomLeftY;
    int topRightX;
    int topRightY;

    Rect(int x, int y, int height, int width) {
        this.bottomLeftX = x;
        this.bottomLeftY = y;
        this.topRightX = x + width;
        this.topRightY = y + height;
    }

    int centerX() {
        return (topRightX + bottomLeftX) / 2;
    }

    int centerY() {
        return (bottomLeftY + topRightY) / 2;
    }

    boolean intersect(Rect other) {
        return (this.bottomLeftX <= other.topRightX && this.topRightX >= other.bottomLeftX
        && this.bottomLeftY <= other.topRightY && this.topRightY >= other.bottomLeftY);
    }
}

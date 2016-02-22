package at.juggle.gdx;

import com.badlogic.gdx.math.Vector3;

/**
 * Created by mlux on 22.02.2016.
 */
public class DodgeDotsGamepadInput {
    enum Direction {Up, Down, Left, Right, None};
    Vector3 north, south, east, west;
    float x0, y0;
    float gpWidth, gpHeight;

    public DodgeDotsGamepadInput(float x0, float y0, float gamepadWidth, float gamepadHeight) {
        this.x0 = x0;
        this.y0 = y0;
        this.gpWidth = gamepadWidth;
        this.gpHeight = gamepadHeight;

        north = new Vector3(x0 + gpWidth / 2, y0 + gpHeight, 0);
        south = new Vector3(x0 + gpWidth / 2, y0, 0);
        west = new Vector3(x0, y0 + gpWidth / 2, 0);
        east = new Vector3(x0 + gpWidth, y0 + gpHeight / 2, 0);
    }

    public Direction handleTouch(Vector3 touch) {
        if (touch.dst(x0 + gpWidth / 2, y0 + gpHeight / 2, touch.z) < gpWidth) {
            // find which is nearest.
            float[] d = new float[4];
            d[0] = touch.dst(north);
            d[1] = touch.dst(east);
            d[2] = touch.dst(south);
            d[3] = touch.dst(west);

            float min = d[0];
            int index = 0;
            for (int i = 1; i < d.length; i++) {
                if (d[i] < min) {
                    index = i;
                    min = d[i];
                }
            }

            // now move ...
            switch (index) {
                case 0:
                    return Direction.Up;
                case 1:
                    return Direction.Right;
                case 2:
                    return Direction.Down;
                case 3:
                    return Direction.Left;
            }

        }
        return Direction.None;
    }

    public float getX() {
        return x0;
    }

    public float getY() {
        return y0;
    }
}

import java.awt.*;

public class Player {
    private int key;
    private boolean pressPrev = false;
    private double x, y, w, h, dx = 3, dy = 2.5, finishX;

    public Player(int x, int y, int w, int h, int finishX, int key) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.finishX = finishX;
        this.key = key;
    }

    public void draw(Graphics g, Image texture) {
        g.drawImage(texture, (int) x, (int) y, (int) w, (int) h, null);
    }

    public void move() {
        if (!Main.win) {
            if (Keyboard.getKey(key)) {
                if (!pressPrev && !crossLine()) {
                    x += dx * 3;
                    y += dy * 3;
                    pressPrev = true;
                }
            } else
                pressPrev = false;
        }
    }

    private boolean crossLine() {
        return x >= finishX;
    }

    public boolean win() {
        return crossLine();
    }
}

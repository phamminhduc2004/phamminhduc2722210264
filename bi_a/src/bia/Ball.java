package bia;

import java.awt.*;
import java.util.Random;

class Ball implements Runnable {
    final int id;
    double x, y;
    double dx, dy;
    double speed;
    final int radius = 20;
    final Color color;
    volatile boolean inHole = false;
    final BilliardsSimulation panel;
    final Random rand = new Random();

    Ball(int id, BilliardsSimulation p, double x, double y, double dx, double dy) {
        this.id = id;
        this.panel = p;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
        this.speed = 4; // tốc độ ban đầu

        // Màu ngẫu nhiên (đảm bảo không quá tối)
        this.color = new Color(50 + rand.nextInt(206), 50 + rand.nextInt(206), 50 + rand.nextInt(206));
    }

    public boolean isInHole() {
        return inHole;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillOval((int) (x - radius), (int) (y - radius), radius * 2, radius * 2);
        g.setColor(Color.WHITE);
        g.drawString(String.valueOf(id), (int) x - 5, (int) y + 5);
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (!inHole && speed > 0.05) {
            move();
            panel.checkCollisions(this);

            // Giảm tốc dần sau 1 phút
         // Giảm tốc nhẹ mỗi vòng (ma sát)
            speed *= 0.999;
            if (speed < 0.05) speed = 0;

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void move() {
        x += dx * speed;
        y += dy * speed;

        // Va chạm tường
        if (x - radius < 0 || x + radius > panel.width) dx = -dx;
        if (y - radius < 0 || y + radius > panel.height) dy = -dy;

        // Rơi vào lỗ giữa
        double distToHole = Math.hypot(x - panel.holeX, y - panel.holeY);
        if (distToHole < panel.holeRadius) {
            inHole = true;
        }
    }
}

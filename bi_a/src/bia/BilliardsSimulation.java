package bia;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class BilliardsSimulation extends JPanel {
    ArrayList<Ball> balls = new ArrayList<>();
    int width = 800, height = 500;
    int holeX = width / 2, holeY = height / 2, holeRadius = 30;

    public BilliardsSimulation() {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.WHITE);

        // Tạo 8 quả bóng khác màu, vị trí cố định (không random)
        for (int i = 0; i < 8; i++) {
            // tạo vị trí ban đầu cố định
            double x = 150 + i * 80; // trải đều theo chiều ngang
            double y = 250;          // nằm giữa bàn
            double dx = (i % 2 == 0) ? 1 : -1; // xen kẽ hướng
            double dy = (i < 4) ? 1 : -1;      // 4 viên hướng lên, 4 viên hướng xuống
            Ball b = new Ball(i + 1, this, x, y, dx, dy);
            balls.add(b);
            new Thread(b).start();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Vẽ khung bàn
        g.setColor(Color.RED);
        g.drawRect(1, 1, width - 2, height - 2);

        // Vẽ lỗ ở giữa bàn
        g.setColor(Color.BLACK);
        g.fillOval(holeX - holeRadius, holeY - holeRadius, holeRadius * 2, holeRadius * 2);

        // Vẽ bóng
        for (Ball b : balls) {
            if (!b.isInHole()) {
                b.draw(g);
            }
        }
    }

    public synchronized void checkCollisions(Ball ball) {
        for (Ball other : balls) {
            if (other != ball && !other.isInHole() && !ball.isInHole()) {
                double dx = other.x - ball.x;
                double dy = other.y - ball.y;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < ball.radius * 2) {
                    // Chuẩn hóa vector va chạm
                    double nx = dx / distance;
                    double ny = dy / distance;

                    // Tách 2 quả bi ra tránh dính nhau
                    double overlap = ball.radius * 2 - distance;
                    ball.x -= nx * overlap / 2;
                    ball.y -= ny * overlap / 2;
                    other.x += nx * overlap / 2;
                    other.y += ny * overlap / 2;

                    // Thành phần vận tốc dọc theo hướng va chạm
                    double kx = ball.dx - other.dx;
                    double ky = ball.dy - other.dy;
                    double p = 2 * (kx * nx + ky * ny) / 2; // chia 2 vì khối lượng bằng nhau

                    // Cập nhật hướng vận tốc
                    ball.dx -= p * nx;
                    ball.dy -= p * ny;
                    other.dx += p * nx;
                    other.dy += p * ny;

                    // Giữ tốc độ gần như cũ (mất rất ít do ma sát)
                    ball.speed *= 0.99;
                    other.speed *= 0.99;
                }
            }
        }
    }


    public static void main(String[] args) {
        JFrame frame = new JFrame("Lập trình song song - Bài toán bi-a");
        JPanel mainPanel = new JPanel(new BorderLayout());
        BilliardsSimulation table = new BilliardsSimulation();
        mainPanel.add(table, BorderLayout.CENTER);

        frame.add(mainPanel);
        frame.setSize(1000, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // Giữa màn hình
        frame.setVisible(true);

        // Cập nhật lại giao diện 60 lần/giây
        new Timer(16, e -> table.repaint()).start();
    }
}

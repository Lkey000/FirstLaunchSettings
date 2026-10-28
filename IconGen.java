import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class IconGen {
    static final int S = 400;

    static int hash(int x, int y, int seed) {
        int h = x * 374761393 + y * 668265263 + seed * 1442695041;
        h = (h ^ (h >>> 13)) * 1274126177;
        return (h ^ (h >>> 16)) & 0x7fffffff;
    }

    static Color shade(Color c, double f) {
        return new Color(Math.min(255, (int) (c.getRed() * f)), Math.min(255, (int) (c.getGreen() * f)), Math.min(255, (int) (c.getBlue() * f)));
    }

    static Color grass(int i, int j, int seed) {
        Color[] p = {new Color(0x6FB43C), new Color(0x62A533), new Color(0x7CC247), new Color(0x58952C)};
        return p[hash(i, j, seed) % p.length];
    }

    static Color dirt(int i, int j, int seed) {
        Color[] p = {new Color(0x8B5E3C), new Color(0x7A4F30), new Color(0x9A6B45), new Color(0x6D4529), new Color(0x84573A)};
        return p[hash(i, j, seed) % p.length];
    }

    // 16x16のテクスチャを平行四辺形に貼る（face: 0=上 1=左 2=右）
    static void face(Graphics2D g, double ox, double oy, double ux, double uy, double vx, double vy, int face, double light) {
        int N = 16;
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                Color c;
                if (face == 0) {
                    c = grass(i, j, 1);
                } else {
                    int fringe = 3 + (hash(i, 0, 7 + face) % 3 == 0 ? 1 : 0) - (hash(i, 1, 9 + face) % 4 == 0 ? 1 : 0);
                    c = (j < fringe) ? grass(i, j, 3 + face) : dirt(i, j, 5 + face);
                }
                c = shade(c, light);
                Path2D.Double q = new Path2D.Double();
                double ax = ox + (i * ux + j * vx) / N, ay = oy + (i * uy + j * vy) / N;
                q.moveTo(ax, ay);
                q.lineTo(ax + ux / N, ay + uy / N);
                q.lineTo(ax + (ux + vx) / N, ay + (uy + vy) / N);
                q.lineTo(ax + vx / N, ay + vy / N);
                q.closePath();
                g.setColor(c);
                g.fill(q);
                g.setStroke(new BasicStroke(0.8f));
                g.draw(q);
            }
        }
    }

    static void badge(Graphics2D g, int cx, int cy, int r, Color fill) {
        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new Ellipse2D.Double(cx - r - 2, cy - r + 4, (r + 6) * 2 - 2, (r + 6) * 2 - 2));
        g.setColor(Color.WHITE);
        g.fill(new Ellipse2D.Double(cx - r - 5, cy - r - 5, (r + 5) * 2, (r + 5) * 2));
        g.setColor(fill);
        g.fill(new Ellipse2D.Double(cx - r, cy - r, r * 2, r * 2));
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        BufferedImage img = new BufferedImage(S, S, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // 背景
        Shape bg = new RoundRectangle2D.Double(0, 0, S, S, 84, 84);
        g.setPaint(new GradientPaint(0, 0, new Color(0x141B3D), S, S, new Color(0x2B2466)));
        g.fill(bg);
        g.setClip(bg);
        // 背景のドット模様（ピクセル感）
        g.setColor(new Color(255, 255, 255, 14));
        for (int y = 12; y < S; y += 24) for (int x = 12; x < S; x += 24) g.fillRect(x, y, 4, 4);
        // キューブ背後のグロー
        g.setPaint(new RadialGradientPaint(new Point2D.Double(200, 165), 190f, new float[]{0f, 1f},
                new Color[]{new Color(0x3FD0C9 & 0xFFFFFF | (110 << 24), true), new Color(0x3FD0C9 & 0xFFFFFF, true)}));
        g.fillRect(0, 0, S, S);
        // 上部ハイライト
        g.setPaint(new GradientPaint(0, 0, new Color(255, 255, 255, 40), 0, S / 2f, new Color(255, 255, 255, 0)));
        g.fillRect(0, 0, S, S / 2);
        g.setClip(null);

        // 地面の影
        g.setColor(new Color(0, 0, 0, 80));
        g.fill(new Ellipse2D.Double(105, 262, 190, 34));

        // ブロック（アイソメトリック）
        double w = 96, h = 55, d = 104, tx = 200, ty = 52;
        face(g, tx - w, ty + h, w, h, 0, d, 1, 0.82);            // 左面
        face(g, tx, ty + 2 * h, w, -h, 0, d, 2, 0.62);           // 右面
        face(g, tx, ty, w, h, -w, h, 0, 1.0);                    // 上面
        // 稜線のハイライト
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 255, 255, 90));
        Path2D top = new Path2D.Double();
        top.moveTo(tx - w, ty + h); top.lineTo(tx, ty); top.lineTo(tx + w, ty + h);
        g.draw(top);

        // フルスクリーンを表す四隅のブラケット
        g.setColor(new Color(255, 255, 255, 235));
        g.setStroke(new BasicStroke(14f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int m = 40, L = 48;
        g.draw(new Line2D.Double(m, m + L, m, m));         g.draw(new Line2D.Double(m, m, m + L, m));
        g.draw(new Line2D.Double(S - m - L, m, S - m, m)); g.draw(new Line2D.Double(S - m, m, S - m, m + L));
        g.draw(new Line2D.Double(m, S - m - L, m, S - m)); g.draw(new Line2D.Double(m, S - m, m + L, S - m));
        g.draw(new Line2D.Double(S - m - L, S - m, S - m, S - m)); g.draw(new Line2D.Double(S - m, S - m, S - m, S - m - L));

        // バッジ：日本語「あ」
        int by = 322, br = 38;
        badge(g, 142, by, br, new Color(0x3B6BE0));
        g.setFont(new Font("Dialog", Font.BOLD, 54));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString("あ", 142 - fm.stringWidth("あ") / 2, by - (fm.getHeight() / 2) + fm.getAscent() - 3);

        // バッジ：ミュート（ナレーターOFF）
        int cx = 258, cy = by;
        badge(g, cx, cy, br, new Color(0xE23D4B));
        g.setColor(Color.WHITE);
        Path2D sp = new Path2D.Double();
        sp.moveTo(cx - 23, cy - 9); sp.lineTo(cx - 12, cy - 9); sp.lineTo(cx + 2, cy - 22);
        sp.lineTo(cx + 2, cy + 22); sp.lineTo(cx - 12, cy + 9); sp.lineTo(cx - 23, cy + 9); sp.closePath();
        g.fill(sp);
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.draw(new Line2D.Double(cx + 12, cy - 9, cx + 26, cy + 9));
        g.draw(new Line2D.Double(cx + 26, cy - 9, cx + 12, cy + 9));
        g.dispose();

        ImageIO.write(img, "png", new File("icon_400.png"));   // CurseForge / GitHub用
        BufferedImage small = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = small.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(img.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING), 0, 0, null);
        g2.dispose();
        ImageIO.write(small, "png", new File("logo.png"));      // MOD内蔵ロゴ用
        System.out.println("OK");
    }
}
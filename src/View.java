import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author leonardo
 */
public class View extends JPanel {
    
    public static final double CAMERA_TO_PROJECTION_PLANE_DISTANCE = 1000;
    
    public static final Vec3 gravity = new Vec3(0, 0.25, 0);
    private List<Point> points = new ArrayList<Point>();
    private List<Stick> sticks = new ArrayList<Stick>();
    
    private Point[][] cube = new Point[6][4];
    private Polygon polygon = new Polygon();
    private Vec3 v1 = new Vec3();
    private Vec3 v2 = new Vec3();
    
    public View() {
        addKeyListener(new KeyHandler());
        
        Point a = new Point(this, 200, 100, -1000, 160, 90, -900);
        Point b = new Point(this, 300, 100, -1000);
        Point c = new Point(this, 300, 200, -1000);
        Point d = new Point(this, 200, 200, -1000);

        Point e = new Point(this, 200, 100, -1100); 
        Point f = new Point(this, 300, 100, -1100);
        Point g = new Point(this, 300, 200, -1100);
        Point h = new Point(this, 200, 200, -1100, 210, 305, -1150);
        
        points.add(a);
        points.add(b);
        points.add(c);
        points.add(d);
        
        points.add(e);
        points.add(f);
        points.add(g);
        points.add(h);
        
        Stick stick01 = new Stick(a, b, true);
        Stick stick02 = new Stick(b, c, true);
        Stick stick03 = new Stick(c, d, true);
        Stick stick04 = new Stick(d, a, true);
        
        Stick stick05 = new Stick(e, f, true);
        Stick stick06 = new Stick(f, g, true);
        Stick stick07 = new Stick(g, h, true);
        Stick stick08 = new Stick(h, e, true);
        
        Stick stick09 = new Stick(a, e, true);
        Stick stick10 = new Stick(b, f, true);
        Stick stick11 = new Stick(c, g, true);
        Stick stick12 = new Stick(d, h, true);
        
        Stick stick13 = new Stick(a, g, false);
        Stick stick14 = new Stick(b, h, false);
        Stick stick15 = new Stick(c, e, false);
        Stick stick16 = new Stick(d, f, false);

        Stick stick17 = new Stick(a, c, false);
        Stick stick18 = new Stick(b, g, false);
        Stick stick19 = new Stick(f, h, false);
        Stick stick20 = new Stick(d, e, false);
        Stick stick21 = new Stick(d, g, false);
        Stick stick22 = new Stick(a, f, false);
        
        sticks.add(stick01);
        sticks.add(stick02);
        sticks.add(stick03);
        sticks.add(stick04);
        sticks.add(stick05);
        sticks.add(stick06);
        sticks.add(stick07);
        sticks.add(stick08);
        sticks.add(stick09);
        sticks.add(stick10);
        sticks.add(stick11);
        sticks.add(stick12);
        
        sticks.add(stick13);
        sticks.add(stick14);
        sticks.add(stick15);
        sticks.add(stick16);
        
        sticks.add(stick17);
        sticks.add(stick18);
        sticks.add(stick19);
        sticks.add(stick20);
        sticks.add(stick21);
        sticks.add(stick22);
        
        // cube for rendering
        cube[0][0] = a;
        cube[0][1] = b;
        cube[0][2] = c;
        cube[0][3] = d;

        cube[1][0] = b;
        cube[1][1] = f;
        cube[1][2] = g;
        cube[1][3] = c;

        cube[2][0] = f;
        cube[2][1] = e;
        cube[2][2] = h;
        cube[2][3] = g;

        cube[3][0] = e;
        cube[3][1] = a;
        cube[3][2] = d;
        cube[3][3] = h;

        cube[4][0] = d;
        cube[4][1] = c;
        cube[4][2] = g;
        cube[4][3] = h;

        cube[5][0] = b;
        cube[5][1] = a;
        cube[5][2] = e;
        cube[5][3] = f;
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                update();
                repaint();
            }
        }, 100, 1000 / 60);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw((Graphics2D) g);
    }
    
    public void update() {
        for (Point point : points) {
            point.update();
        }
        for (int i = 0; i < 5; i++) {
            for (Stick stick : sticks) {
                stick.update();
            }
            for (Point point : points) {
                point.updateCollisionWithGround();
            }
        }
    }
    
    public void reset() {
        for (Point point : points) {
            point.reset();
        }
    }
    
    public void draw(Graphics2D g) {
        for (Point point : points) {
            //point.draw(g);
            point.draw3D(g);
        }
//        for (Stick stick : sticks) {
//            if (stick.isVisible()) {
//                //stick.draw(g);
//                stick.draw3D(g);
//            }
//        }

        drawCube(g);
        
        g.drawString("Press space key to simulate again ", 10, 20);
    }
    
    private void drawCube(Graphics2D g) {
        // draw shadow
        for (int face = 0; face < 6; face++) {
            polygon.reset();
            for (int p = 0; p < 4; p++) {
                Point point = cube[face][p];
                double px = CAMERA_TO_PROJECTION_PLANE_DISTANCE * (point.position.x / -point.position.z);
                double py = CAMERA_TO_PROJECTION_PLANE_DISTANCE * (getHeight() / -point.position.z);
                polygon.addPoint((int) px, (int) py);
            }
            g.setColor(Color.DARK_GRAY);
            g.fill(polygon);
        }
        
        // draw cube
        for (int face = 0; face < 6; face++) {
            polygon.reset();
            for (int p = 0; p < 4; p++) {
                Point point = cube[face][p];
                double px = CAMERA_TO_PROJECTION_PLANE_DISTANCE * (point.position.x / -point.position.z);
                double py = CAMERA_TO_PROJECTION_PLANE_DISTANCE * (point.position.y / -point.position.z);
                polygon.addPoint((int) px, (int) py);
            }
            
            // back-face culling
            double v1x = polygon.xpoints[0] - polygon.xpoints[1];
            double v1y = polygon.ypoints[0] - polygon.ypoints[1];
            double v2x = polygon.xpoints[2] - polygon.xpoints[1];
            double v2y = polygon.ypoints[2] - polygon.ypoints[1];
            double cross = v1x * v2y - v1y * v2x;
            if (cross < 0) {
                g.setColor(getBackground());
                g.fill(polygon);
                g.setColor(Color.BLACK);
                g.draw(polygon);
            }
        }
    }
    
    private class KeyHandler extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getID() != KeyEvent.KEY_PRESSED) {
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                reset();
            }
        }
        
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                View view = new View();
                JFrame frame = new JFrame();
                frame.setTitle("Java Verlet Integration 3D test #2");
                frame.getContentPane().add(view);
                frame.setSize(800, 600);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setResizable(false);
                frame.setVisible(true);
                view.requestFocus();
            }
        });
    }    
    
}

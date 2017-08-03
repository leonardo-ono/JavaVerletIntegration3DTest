
import java.awt.Color;
import java.awt.Graphics2D;


/**
 *
 * @author leonardo
 */
public class Point {
    
    public View view;
    public Vec3 position = new Vec3();
    public Vec3 previousPosition = new Vec3();
    public Vec3 velocity = new Vec3();

    public double restitution = 2.725; // <-- very difficult to bounce
    public double friction = 0.5;
    
    public boolean pinned = false;

    public Vec3 positionReset = new Vec3();
    public Vec3 previousPositionReset = new Vec3();
    
    public Point(View view, double x, double y, double z) {
        this(view, x, y, z, x, y, z);
    }
    
    public Point(View view, double x, double y, double z, double prevX, double prevY, double prevZ) {
        this.view = view;
        position.set(x, y, z);
        previousPosition.set(prevX, prevY, prevZ);

        positionReset.set(x, y, z);
        previousPositionReset.set(prevX, prevY, prevZ);
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }
    
    public void reset() {
        position.set(positionReset);
        previousPosition.set(previousPositionReset);
        previousPosition.x += 20 * Math.random() - 10;
        previousPosition.y += 10 * Math.random() - 5;
        previousPosition.z += 20 * Math.random() - 10;
        restitution = 2.0 + 0.8 * Math.random();
    }
    
    public void update() {
        if (pinned) {
            return;
        }
        velocity.set(position);
        velocity.sub(previousPosition);
        previousPosition.set(position);
        position.add(velocity);
        position.add(View.gravity);
    }
    
    public void updateCollisionWithGround() {
        if (pinned) {
            return;
        }
        
        velocity.set(position);
        velocity.sub(previousPosition);
        velocity.scale(friction);
        
        // collision with ground
        if (position.y > view.getHeight()) {
            position.y = view.getHeight();
            previousPosition.y = position.y + (int) (velocity.y * restitution);
            previousPosition.x += (position.x - previousPosition.x) * friction;
            previousPosition.z += (position.z - previousPosition.z) * friction;
        }
        
    }
    
    public void draw(Graphics2D g) {
        g.setColor(Color.RED);
        g.fillOval((int) (position.x - 3), (int) (position.y - 3), 6, 6);
    }

    public void draw3D(Graphics2D g) {
        g.setColor(Color.RED);
        double px = View.CAMERA_TO_PROJECTION_PLANE_DISTANCE * (position.x / -position.z);
        double py = View.CAMERA_TO_PROJECTION_PLANE_DISTANCE * (position.y / -position.z);
        g.fillOval((int) (px - 3), (int) (py - 3), 6, 6);
    }
    
}

package mvc;

//Programmed by Quinn Berrman
//Student number: 20363251;

//Programmed by Quinn Berrman
//Student number: 20363251

import java.util.Random;

import main.MainWindow;
import util.Entity;
import util.Point3f;
import util.Vector3f;

public class AIController extends Controller{
    private static final float RANGE = 0.66f;
    protected Entity entity;
    protected Random r = new Random();
    protected Vector3f lastMovement = new Vector3f(1f,0f,0f);

    public AIController() {

    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void run(Model m) {
        Point3f p = Viewer.worldSpaceToScreen(entity.getCentre(), m.getPlayer().getCentre());
        aimDirection = new Vector3f(1,0,0).rotateCreate(Math.atan2(MainWindow.getH()/2-p.getY(), MainWindow.getW()/2-p.getX()));
        if(entity.isHostile()) {
            if(m.inRangeOfPlayer(entity, RANGE)) {
                setAttackPressed(true);
                moveDirection = new Vector3f();
            } else {
                setAttackPressed(false);
                Vector3f v = aimDirection.roundToOctet();
                switch(r.nextInt(3)) {
                    case 0:
                        v.rotate((r.nextInt(2)*2-1) * (Math.PI/4.0));     //rotates the direction vector by 45 deg either cw or ccw 
                        break;
                    default:
                        break;
                }
                moveDirection = v;
                lastMovement = v;
            }
        } else {
            setAttackPressed(false);
            Vector3f v = lastMovement;
            switch(r.nextInt(8)) {
                case 0:
                    v.rotate((r.nextInt(2)*2-1) * (Math.PI/4.0));     //rotates the direction vector by 45 deg either cw or ccw 
                    break;
                case 1:
                    break;
                default:
                    moveDirection = new Vector3f();
                    return;
            }
            moveDirection = v;
            lastMovement = v;
        }
    }
}

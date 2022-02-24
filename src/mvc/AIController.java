package mvc;

import java.util.Random;

import util.Entity;
import util.Vector3f;

public class AIController extends Controller{
    private static final float RANGE = 0.66f;
    protected Entity entity;
    protected Random r = new Random();
    protected Vector3f lastMovement = new Vector3f(1f,1f,0f);

    public AIController() {

    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void run(Model m) {
        if(entity.isHostile()) {
            if(m.inRangeOfPlayer(entity, RANGE)) {
                setAttackPressed(true);
                pressMoveButtons(new Vector3f());
            } else {
                r.nextInt(4);
                setAttackPressed(false);
                Vector3f v = entity.getCentre().calculateDirectionToPoint(m.getPlayer().getCentre());
                switch(r.nextInt(4)) {
                    case 0:
                        v.rotate((r.nextInt(2)*2-1) * (Math.PI/4.0));     //rotates the direction vector by 45 deg either cw or ccw 
                        break;
                    default:
                        break;
                }
                pressMoveButtons(v);
                lastMovement = v;
            }
        } else {
            setAttackPressed(false);
            Vector3f v = lastMovement;
            switch(r.nextInt(4)) {
                case 0:
                    v.rotate((r.nextInt(2)*2-1) * (Math.PI/4.0));     //rotates the direction vector by 45 deg either cw or ccw 
                    break;
                case 3:
                    return;
                default:
                    break;
            }
            pressMoveButtons(v);
            lastMovement = v;
        }
    }

    protected void pressMoveButtons(Vector3f v) {
        int x = Math.round(v.getX());
        int y = Math.round(v.getY());
        setRightPressed(x==1 ? true : false);
        setLeftPressed(x==-1 ? true : false);
        setUpPressed(y==-1 ? true : false);
        setDownPressed(y==1 ? true : false);
    }
}

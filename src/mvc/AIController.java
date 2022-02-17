package mvc;

import java.util.Random;

import util.Entity;
import util.NPC;
import util.Vector3f;

public class AIController extends Controller{
    private Entity entity;
    private Random r = new Random();
    private Vector3f lastMovement = new Vector3f(1f,1f,0f);

    public AIController() {

    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void run(Model m) {
        //TODO fix npc staggering while chasing
        if(entity.isHostile()) {
            if(m.inRangeOfPlayer(entity)) {
                setKeyQPressed(true);
                pressMoveButtons(new Vector3f());
            } else {
                r.nextInt(4);
                setKeyQPressed(false);
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

    private void pressMoveButtons(Vector3f v) {
        int x = (int)v.getX();
        int y = (int)v.getY();
        setKeyDPressed(x==1 ? true : false);
        setKeyAPressed(x==-1 ? true : false);
        setKeyWPressed(y==-1 ? true : false);
        setKeySPressed(y==1 ? true : false);
    }
}

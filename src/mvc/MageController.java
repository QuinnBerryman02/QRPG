package mvc;

import main.MainWindow;
import util.Vector3f;

public class MageController extends AIController{
    private static final float RANGE = 3;
    @Override
    public void run(Model m) {
        setMouseX(MainWindow.getW()/2);
        setMouseY(MainWindow.getH()/2);
        if(entity.isInCombat()) {
            if(m.inRangeOfPlayer(entity, RANGE)) {
                setKeyEPressed(true);
                pressMoveButtons(new Vector3f());
            } else {
                r.nextInt(4);
                setKeyEPressed(false);
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
            setKeyEPressed(false);
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
}

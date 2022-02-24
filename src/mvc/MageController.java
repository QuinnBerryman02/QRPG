package mvc;

import main.MainWindow;
import util.Point3f;
import util.Vector3f;

public class MageController extends AIController{
    private static final float RANGE = 3;
    @Override
    public void run(Model m) {
        Point3f p = Viewer.worldSpaceToScreen(entity.getCentre(), m.getPlayer().getCentre());
        aimDirection = new Vector3f(1,0,0).rotateCreate(Math.atan2(MainWindow.getH()/2-p.getY(), MainWindow.getW()/2-p.getX()));
        if(entity.isHostile()) {
            if(m.inRangeOfPlayer(entity, RANGE)) {
                setCastPressed(true);
                moveDirection = new Vector3f();
            } else {
                setCastPressed(false);
                Vector3f v = aimDirection.roundToOctet();
                switch(r.nextInt(8)) {
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
            setCastPressed(false);
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
            moveDirection = v;
            lastMovement = v;
        }
    }
}

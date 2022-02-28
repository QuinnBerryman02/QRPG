package util;

//Programmed by Quinn Berrman
//Student number: 20363251


import java.io.Serializable;

public class Hitbox implements Serializable{
    private Point3f topLeft;
    private Point3f topRight;
    private Point3f botLeft;
    private Point3f botRight;

    protected Hitbox() {
        
    }

    public Hitbox(Point3f tl, Point3f tr, Point3f bl, Point3f br) {
        topLeft = tl;
        topRight = tr;
        botLeft = bl;
        botRight = br;
    }

    public Hitbox (Point3f c, float w, float h) {
        float cx = c.getX();
        float cy = c.getY();
        topLeft =new Point3f(cx-w/2f,cy-h/2f,0);
        topRight=new Point3f(cx+w/2f,cy-h/2f,0);
        botLeft =new Point3f(cx-w/2f,cy+h/2f,0);
        botRight=new Point3f(cx+w/2f,cy+h/2f,0);
    }

    public Vector3f intersection(Hitbox h, Vector3f v) {
        for(Point3f c1 : getCorners()) {
            Point3f c2 = c1.plusVector(v);
            if(isColliding(c2, h)) {
                // System.out.printf("c1:%s\nv:%s\nc2:%s\n",c1.toString(),v.toString(),c2.toString());
                // System.out.printf("hrx:%f c2x:%f hlx:%f\n",h.getRightX(),c2.getX(),h.getLeftX());
                // System.out.printf("hby:%f c2y:%f hty:%f\n",h.getBotY(),c2.getY(),h.getTopY());
                float intersectionX = (v.getX() < 0) ? (h.getRightX() - c2.getX()) : (c2.getX() - h.getLeftX());
                float intersectionY = (v.getY() < 0) ? (h.getBotY() - c2.getY()) : (c2.getY() - h.getTopY());
                float totalDiffX = Math.abs(v.getX());
                float totalDiffY = Math.abs(v.getY());
                float percent;
                //System.out.printf("ix:%f iy:%f tdx:%f tdy:%f\n",intersectionX,intersectionY,totalDiffX,totalDiffY);
                if(totalDiffX >= intersectionX) {
                    float newDiffX = totalDiffX - intersectionX;
                    percent = newDiffX / totalDiffX;
                } else if (totalDiffY >= intersectionY) {
                    float newDiffY = totalDiffY - intersectionY;
                    percent = newDiffY / totalDiffY;
                } else {
                    return v.byScalar(0);
                }
                return v.byScalar(percent);
            }
        }
        return null;
    }

    public boolean isColliding(Hitbox h) {
        for (Point3f p : getCorners()) {
            if(isColliding(p, h)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isColliding(Point3f p, Hitbox h) {
        return (p.getX() < h.getRightX() && p.getX() > h.getLeftX() && p.getY() < h.getBotY() && p.getY() > h.getTopY());
    }

    public Point3f[] getCorners() {
        return new Point3f[] {topLeft, topRight, botLeft, botRight};
    }

    public float getLeftX() {
        return botLeft.getX();
    }
    public float getRightX() {
        return topRight.getX();
    }
    public float getTopY() {
        return topLeft.getY();
    }
    public float getBotY() {
        return botRight.getY();
    }

    public void applyVector(Vector3f v) {
        for (Point3f c : getCorners()) {
            c.applyVector(v);
        }
    }

    public Hitbox plusVector(Vector3f v) {
        return new Hitbox(
            topLeft.plusVector(v), 
            topRight.plusVector(v),
            botLeft.plusVector(v), 
            botRight.plusVector(v)
        );
    }

    @Override
    public String toString() {
        String s = "";
        for (Point3f corner : getCorners()) {
            s += corner.toString();
            s += "\n";
        }
        return s;
    }
}

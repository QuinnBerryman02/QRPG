package util;

public class Segment {
    private Point3f p1;
    private Point3f p2;
    public Segment(Point3f p1, Point3f p2) {
        this.p1 = p1.plusVector(new Vector3f());
        this.p2 = p2.plusVector(new Vector3f());
    }
    public Point3f getP1() {
        return p1;
    }

    public Point3f getP2() {
        return p2;
    }
}
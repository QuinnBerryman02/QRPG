package util;

import mvc.Controller;

public class NPC extends Entity {
    private String name;
    //private Face face;

    public NPC(float w, float h, Point3f c, float speed, Skin skin, String name, Controller controller) {
        super(skin, w, h, c, speed, controller);
        this.name = name;
    }
}

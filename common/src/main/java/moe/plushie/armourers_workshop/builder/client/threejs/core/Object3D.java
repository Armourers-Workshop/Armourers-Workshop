package moe.plushie.armourers_workshop.builder.client.threejs.core;

import moe.plushie.armourers_workshop.utils.math.OpenQuaternionf;
import moe.plushie.armourers_workshop.utils.math.Vector3f;

public class Object3D {

    public Vector3f up = new Vector3f();

    public Vector3f position = new Vector3f();
//    val rotation: Euler
    public OpenQuaternionf quaternion;
    public Vector3f scale;


    /**
     * Rotates object to face point in space.
     * @param vector A world vector to look at.
     */
    public void lookAt(Vector3f vector) {
        lookAt(vector.getX(), vector.getY(), vector.getZ());
    }

    /**
     * Rotates object to face point in space.
     */
    public void lookAt(float x, float y, float z) {

    }


}

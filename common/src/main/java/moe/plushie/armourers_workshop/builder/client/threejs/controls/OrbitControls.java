package moe.plushie.armourers_workshop.builder.client.threejs.controls;

import moe.plushie.armourers_workshop.builder.client.threejs.camera.Camera;
import moe.plushie.armourers_workshop.builder.client.threejs.core.EventSource;
import moe.plushie.armourers_workshop.utils.MathUtils;
import moe.plushie.armourers_workshop.utils.math.*;

public class OrbitControls {

    private static final float EPS = 0.000001f;

    // Set to false to disable this control
    public boolean enabled = true;

    // "target" sets the location of focus, where the object orbits around
    public Vector3f target = new Vector3f();

    // How far you can dolly in and out ( PerspectiveCamera only )
    public float minDistance = 0f;
    public float maxDistance = Float.POSITIVE_INFINITY;

    // How far you can zoom in and out ( OrthographicCamera only )
    public float minZoom = 0f;
    public float maxZoom = Float.POSITIVE_INFINITY;

    // How far you can orbit vertically, upper and lower limits.
    // Range is 0 to Math.PI radians.
    public float minPolarAngle = 0f; // radians
    public float maxPolarAngle = MathUtils.PI; // radians

    // How far you can orbit horizontally, upper and lower limits.
    // If set, must be a sub-interval of the interval [ - Math.PI, Math.PI ].
    public float minAzimuthAngle = Float.NEGATIVE_INFINITY; // radians
    public float maxAzimuthAngle = Float.POSITIVE_INFINITY; // radians

    // Set to true to enable damping (inertia)
    // If damping is enabled, you must call controls.update() in your animation loop
    public boolean enableDamping = false;
    public float dampingFactor = 0.05f;

    // This option actually enables dollying in and out; left as "zoom" for backwards compatibility.
    // Set to false to disable zooming
    public boolean enableZoom = true;
    public float zoomSpeed = 1.0f;

    // Set to false to disable rotating
    public boolean enableRotate = true;
    public float rotateSpeed = 1.0f;

    // Set to false to disable panning
    public boolean enablePan = true;
    public float panSpeed = 1.0f;
    public boolean screenSpacePanning = false; // if true, pan in screen-space
    public float keyPanSpeed = 7f; // pixels moved per arrow key push

    // Set to true to automatically rotate around the target
    // If auto-rotate is enabled, you must call controls.update() in your animation loop
    public boolean autoRotate = false;
    public float autoRotateSpeed = 2.0f; // 30 seconds per round when fps is 60

    // Set to false to disable use of the keys
    public boolean enableKeys = true;

//    // for reset
//    private var target0 = target.clone()
//    private var position0 = camera.position.clone()
//    private var zoom0 = (camera as CameraWithZoom).zoom

    // current position in spherical coordinates
    private Sphericalf spherical = new Sphericalf();
    private Sphericalf sphericalDelta = new Sphericalf();

    private float scale = 1f;
    private Vector3f panOffset = new Vector3f();
    private boolean zoomChanged = false;

    private Vector2f rotateStart = new Vector2f();
    private Vector2f rotateEnd = new Vector2f();
    private Vector2f rotateDelta = new Vector2f();

    private Vector2f panStart = new Vector2f();
    private Vector2f panEnd = new Vector2f();
    private Vector2f panDelta = new Vector2f();

    private Vector2f dollyStart = new Vector2f();
    private Vector2f dollyEnd = new Vector2f();
    private Vector2f dollyDelta = new Vector2f();

    private State state = State.NONE;
    private Updater updater = new Updater();

    private EventSource.KeyListener defaultKeyListener = null;
    private EventSource.MouseListener defaultMouseListener = null;

    private final Camera camera;
    private final EventSource eventSource;

    public OrbitControls(Camera camera, EventSource eventSource) {
        this.camera = camera;
        this.eventSource = eventSource;

        update();

        eventSource.addKeyListener(defaultKeyListener);
        eventSource.addMouseListener(defaultMouseListener);
    }

    public float getPolarAngle() {
        return spherical.phi;

    }

    public float getAzimuthalAngle() {
        return spherical.theta;
    }

    public void saveState() {
        // TODO: @SAGESSE
//        this.target0.copy(this.target);
//        this.position0.copy(this.camera.position);
//        this.zoom0 = (this.camera as CameraWithZoom).zoom;
    }

    public void reset() {
        // TODO: @SAGESSE
//        this.target.copy(this.target0)
//        this.camera.position.copy(this.position0)
//        (this.camera as CameraWithZoom).zoom = this.zoom0
//
//                when (camera) {
//            is PerspectiveCamera -> camera.updateProjectionMatrix()
//            is OrthographicCamera -> camera.updateProjectionMatrix()
//            else -> throw UnsupportedOperationException()
//        }
//
//        this.dispatchEvent("change", this)
//
//        this.update()
//
//        state = State.NONE
    }

    // this method is exposed, but perhaps it would be better if we can make it private...
    public boolean update() {
        return updater.update();
    }

    public float getAutoRotationAngle() {
        return 2 * MathUtils.PI / 60 / 60 * autoRotateSpeed;

    }

    public float getZoomScale() {
        return (float) Math.pow(0.95, zoomSpeed);
    }

    public void rotateLeft(float angle) {
        sphericalDelta.theta -= angle;
    }

    public void rotateUp(float angle) {
        sphericalDelta.phi -= angle;

    }

    public void panLeft(float distance, Matrix4f matrix) {
        Vector3f v = new Vector3f(matrix.m00, matrix.m01, matrix.m02);
        v.mul(-distance);
        panOffset.add(v);
    }

    public void panUp(float distance, Matrix4f matrix) {
        Vector3f v;
        if (screenSpacePanning) {
            v = new Vector3f(matrix.m10, matrix.m11, matrix.m12);
        } else {
            v = new Vector3f(matrix.m00, matrix.m01, matrix.m02);
            v.cross(camera.up);
        }
        v.mul(distance);
        panOffset.add(v);
    }

//    // deltaX and deltaY are in pixels; right and down are positive
//    fun pan(deltaX: Float, deltaY: Float) {
//
//        val offset = Vector3()
//
//                when {
//            this.camera is PerspectiveCamera -> {
//
//                // perspective
//                val position = this.camera.position
//                        offset.copy(position).sub(this.target)
//                var targetDistance = offset.length()
//
//                        // half of the fov is center to top of screen
//                        targetDistance *= tan((this.camera.fov / 2) * PI.toFloat() / 180f)
//
//                        // we use only clientHeight here so aspect ratio does not distort speed
//                        panLeft(2 * deltaX * targetDistance / eventSource.size.height, this.camera.matrix)
//                panUp(2 * deltaY * targetDistance / eventSource.size.height, this.camera.matrix)
//
//            }
//            this.camera is OrthographicCamera -> {
//                // orthographic
//                panLeft(
//                        deltaX * (this.camera.right - this.camera.left) / this.camera.zoom / eventSource.size.width,
//                        this.camera.matrix
//                )
//                panUp(
//                        deltaY * (this.camera.top - this.camera.bottom) / this.camera.zoom / eventSource.size.height,
//                        this.camera.matrix
//                )
//            }
//            else -> {
//
//                // camera neither orthographic nor perspective
//                LOG.warn("encountered an unknown camera type - pan disabled.")
//                this.enablePan = false
//
//            }
//        }
//
//    }
//
//    fun dollyIn(dollyScale: Float) {
//
//        when {
//            this.camera is PerspectiveCamera -> scale /= dollyScale
//            this.camera is OrthographicCamera -> {
//
//                this.camera.zoom = max(this.minZoom, min(this.maxZoom, this.camera.zoom * dollyScale))
//                this.camera.updateProjectionMatrix()
//                zoomChanged = true
//
//            }
//            else -> {
//
//                LOG.warn("encountered an unknown camera type - dolly/zoom disabled.")
//                this.enableZoom = false
//
//            }
//        }
//
//    }
//
//    fun dollyOut(dollyScale: Float) {
//
//        when {
//            this.camera is PerspectiveCamera -> scale *= dollyScale
//            this.camera is OrthographicCamera -> {
//
//                this.camera.zoom = max(this.minZoom, min(this.maxZoom, this.camera.zoom / dollyScale))
//                this.camera.updateProjectionMatrix()
//                zoomChanged = true
//
//            }
//            else -> {
//
//                LOG.warn("encountered an unknown camera type - dolly/zoom disabled.")
//                this.enableZoom = false
//
//            }
//        }
//
//    }
//
//    private fun handleKeyDown(event: KeyEvent) {
//
//        var needsUpdate = true
//
//                when (event.keyCode) {
//            Keys.UP -> {
//                pan(0f, keyPanSpeed)
//            }
//            Keys.BOTTOM -> {
//                pan(0f, -keyPanSpeed)
//            }
//            Keys.LEFT -> {
//                pan(keyPanSpeed, 0f)
//            }
//            Keys.RIGHT -> {
//                pan(-keyPanSpeed, 0f)
//            }
//            else -> needsUpdate = false
//        }
//
//        if (needsUpdate) {
//            this.update()
//        }
//
//    }
//
//    private fun handleMouseDownRotate(event: MouseEvent) {
//        rotateStart.set(event.clientX.toFloat(), event.clientY.toFloat())
//    }
//
//    private fun handleMouseDownDolly(event: MouseEvent) {
//        dollyStart.set(event.clientX.toFloat(), event.clientY.toFloat())
//    }
//
//    private fun handleMouseDownPan(event: MouseEvent) {
//        panStart.set(event.clientX.toFloat(), event.clientY.toFloat())
//    }
//
//
//    private fun handleMouseMoveRotate(event: MouseEvent) {
//        rotateEnd.set(event.clientX.toFloat(), event.clientY.toFloat())
//
//        rotateDelta.subVectors(rotateEnd, rotateStart).multiplyScalar(rotateSpeed)
//
//        rotateLeft(2 * PI.toFloat() * rotateDelta.x / eventSource.size.width) // yes, height
//
//        rotateUp(2 * PI.toFloat() * rotateDelta.y / eventSource.size.height)
//
//        rotateStart.copy(rotateEnd)
//
//        update()
//    }
//
//    private fun handleMouseMoveDolly(event: MouseEvent) {
//        dollyEnd.set(event.clientX.toFloat(), event.clientY.toFloat())
//
//        dollyDelta.subVectors(dollyEnd, dollyStart)
//
//        if (dollyDelta.y > 0) {
//
//            dollyIn(getZoomScale())
//
//        } else if (dollyDelta.y < 0) {
//
//            dollyOut(getZoomScale())
//
//        }
//
//        dollyStart.copy(dollyEnd)
//
//        update()
//    }
//
//    private fun handleMouseMovePan(event: MouseEvent) {
//        panEnd.set(event.clientX.toFloat(), event.clientY.toFloat())
//
//        panDelta.subVectors(panEnd, panStart).multiplyScalar(panSpeed)
//
//        pan(panDelta.x, panDelta.y)
//
//        panStart.copy(panEnd)
//
//        update()
//    }
//
//    private fun handleMouseWheel(event: MouseWheelEvent) {
//
//        if (event.deltaY < 0) {
//
//            dollyOut(getZoomScale())
//
//        } else if (event.deltaY > 0) {
//
//            dollyIn(getZoomScale())
//
//        }
//
//        update()
//
//    }

    public void dispose() {
        eventSource.removeKeyListener(defaultKeyListener);
        eventSource.removeMouseListener(defaultMouseListener);
    }

    private class Updater {

        private Vector3f offset = new Vector3f();

        private Vector3f lastPosition = new Vector3f();
        private Quaternionf lastQuaternion = new Quaternionf();

        // so camera.up is the orbit axis
        private Quaternionf quat = Quaternionf.fromUnitVectors(camera.up, new Vector3f(0, 1, 0));
        private Quaternionf quatInverse = quat.copy().inverse();

        public boolean update() {
            Vector3f position = camera.position;
            offset.set(position);
            offset.sub(target);

            // rotate offset to "y-axis-is-up" space
            offset.transform(quat);

            // angle from z-axis around y-axis
            spherical.setFromVector3(offset);

            if (autoRotate && state == State.NONE) {
                rotateLeft(getAutoRotationAngle());
            }

            if (enableDamping) {
                spherical.theta += sphericalDelta.theta * dampingFactor;
                spherical.phi += sphericalDelta.phi * dampingFactor;

            } else {
                spherical.theta += sphericalDelta.theta;
                spherical.phi += sphericalDelta.phi;
            }

            // restrict theta to be between desired limits
            spherical.theta = MathUtils.clamp(spherical.theta, minAzimuthAngle, maxAzimuthAngle);

            // restrict phi to be between desired limits
            spherical.phi = MathUtils.clamp(spherical.phi, minPolarAngle, maxPolarAngle);

            spherical.makeSafe();
            spherical.radius *= scale;

            // restrict radius to be between desired limits
            spherical.radius = MathUtils.clamp(spherical.radius, minDistance, maxDistance);

            // move target to panned location
            if (enableDamping) {
                Vector3f tmp = new Vector3f(panOffset);
                tmp.mul(dampingFactor);
                target.add(tmp);
            } else {
                target.add(panOffset);
            }

            offset.setFromSpherical(spherical);

            // rotate offset back to "camera-up-vector-is-up" space
            offset.transform(quatInverse);

            position.set(target);
            position.add(offset);

            camera.lookAt(target);

            if (enableDamping) {
                sphericalDelta.theta *= (1 - dampingFactor);
                sphericalDelta.phi *= (1 - dampingFactor);
                panOffset.mul(1 - dampingFactor);
            } else {
                sphericalDelta.set(0f, 0f, 0f);
                panOffset.set(0, 0, 0);
            }

            scale = 1f;

            // update condition is:
            // min(camera displacement, camera rotation in radians)^2 > EPS
            // using small-angle approximation cos(x/2) = 1 - x^2 / 8

            if (zoomChanged || lastPosition.distanceToSquared(camera.position) > EPS || 8 * (1 - lastQuaternion.dot(camera.quaternion)) > EPS) {

//            this.dispatchEvent("change", this);

                lastPosition.set(camera.position);
                lastQuaternion.set(camera.quaternion);
                zoomChanged = false;
                return true;
            }
            return false;
        }
    }

    private enum State {
        NONE, ROTATE, DOLLY, PAN,
    }
}

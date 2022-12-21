package moe.plushie.armourers_workshop.builder.client.gui.advancedskinbuilder;

import com.apple.library.coregraphics.CGPoint;

public class OrbitControls {

    class Spherical {

        float theta = 0;
        float phi = 0;
    }

    	 float rotateSpeed = 1f;

    float minPolarAngle = 0; // radians
    float maxPolarAngle = (float) Math.PI; // radians
    float minAzimuthAngle = Float.NEGATIVE_INFINITY; // radians
    float maxAzimuthAngle = Float.POSITIVE_INFINITY; // radians


    CGPoint rotateStart = CGPoint.ZERO;
    CGPoint rotateEnd = CGPoint.ZERO;
    CGPoint rotateDelta = CGPoint.ZERO;

    // current position in spherical coordinates
    Spherical spherical = new Spherical();
    Spherical sphericalDelta = new Spherical();

    float clientWidth;
    float clientHeight;

    public void startRotate(CGPoint point) {
        rotateStart = point;
    }

    public void updateRotate(CGPoint point) {
        rotateEnd = point;
        rotateDelta = new CGPoint(rotateEnd.x - rotateStart.x, rotateEnd.y - rotateStart.y);

        // rotating across whole screen goes 360 degrees around
        this.rotateLeft( 2 * Math.PI * rotateDelta.x / this.clientWidth * this.rotateSpeed );

        // rotating up and down along whole screen attempts to go 360, but limited to 180
        this.rotateUp( 2 * Math.PI * rotateDelta.y / this.clientHeight * this.rotateSpeed );

        rotateStart = rotateEnd;

        this.update();

//        scope.updateSceneScale();

    }

    public void endRotate(CGPoint point) {

    }

    private void rotateLeft(double angle) {
        sphericalDelta.theta -= angle;
    }

    private void rotateUp(double angle) {
        sphericalDelta.phi -= angle;
    }


    private void update() {
//        if (scope.unlinked) return false;
//
//        var position = scope.object.position;
//
//        offset.copy( position ).sub( scope.target );
//
//        // rotate offset to "y-axis-is-up" space
//        offset.applyQuaternion( quat );
//
        // angle from z-axis around y-axis
//        spherical.setFromVector3( offset );

//        if ( scope.autoRotate && state === STATE.NONE ) {
//
//            let auto_rot_angle = getAutoRotationAngle()
//                    scope.autoRotateProgress += auto_rot_angle;
//            scope.rotateLeft( auto_rot_angle );
//
//        }
//
        spherical.theta += sphericalDelta.theta;
        spherical.phi += sphericalDelta.phi;

        // restrict theta to be between desired limits
        spherical.theta = Math.max( this.minAzimuthAngle, Math.min( this.maxAzimuthAngle, spherical.theta ) );

        // restrict phi to be between desired limits
        spherical.phi = Math.max( this.minPolarAngle, Math.min( this.maxPolarAngle, spherical.phi ) );

//        spherical.makeSafe();
//
//
//        spherical.radius *= scale;
//
//        // restrict radius to be between desired limits
//        spherical.radius = Math.max( scope.minDistance, Math.min( scope.maxDistance, spherical.radius ) );
//
//        // move target to panned location
//        scope.target.add( panOffset );
//
//        offset.setFromSpherical( spherical );
//
//        // rotate offset back to "camera-up-vector-is-up" space
//        offset.applyQuaternion( quatInverse );
//
//        position.copy( scope.target ).add( offset );
//
//        scope.object.lookAt( scope.target );
//
//        if ( scope.enableDamping === true ) {
//
//            sphericalDelta.theta *= ( 1 - scope.dampingFactor );
//            sphericalDelta.phi *= ( 1 - scope.dampingFactor );
//
//        } else {
//
//            sphericalDelta.set( 0, 0, 0 );
//
//        }
//
//        scale = 1;
//        panOffset.set( 0, 0, 0 );
//
//        // update condition is:
//        // min(camera displacement, camera rotation in radians)^2 > EPS
//        // using small-angle approximation cos(x/2) = 1 - x^2 / 8
//
//
//        if ( zoomChanged ||
//                lastPosition.distanceToSquared( scope.object.position ) > EPS ||
//                8 * ( 1 - lastQuaternion.dot( scope.object.quaternion ) ) > EPS ) {
//
//            scope.dispatchEvent( changeEvent );
//
//            lastPosition.copy( scope.object.position );
//            lastQuaternion.copy( scope.object.quaternion );
//            zoomChanged = false;
//
//            updateHandlers.forEach(call => call(changeEvent));
//
//            return true;
//
//        }
//
//        return false;
    }
//	this.rotateSpeed = 1.0;
//	this.enablePan = true;
//	this.keyPanSpeed = 7.0;	// pixels moved per arrow key push
//	this.autoRotate = false;
//	this.autoRotateSpeed = 2.0; // 30 seconds per round when fps is 60
//	this.enableKeys = true;

    // 		rotateStart.set( event.clientX, event.clientY );


//    function handleMouseMoveRotate( event ) {
//        rotateEnd.set( event.clientX, event.clientY );
//        rotateDelta.subVectors( rotateEnd, rotateStart );
//
//        var element = scope.domElement === document ? scope.domElement.body : scope.domElement;
//    }

}

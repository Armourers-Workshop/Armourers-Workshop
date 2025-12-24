package moe.plushie.armourers_workshop.core.skin.particle;

import moe.plushie.armourers_workshop.core.utils.OpenPrimitive;

/**
 * Curves are interpolation values with inputs from 0 to 1, and outputs based on the curve.
 * For each rendering frame of each particle, the curves are evaluated and the result is placed in a Molang variable with the same name as the curve.
 * <p>
 * Molangvar is the Molang variable to be used in Molang expressions.
 * For example, variable.mycurve in the example below would make the result of the curve available in Molang as variable.mycurve.
 * All variables must begin with variable.
 * <p>
 * The type can be "linear", "bezier", "bezier_chain", or "catmull_rom".
 * <a href="https://learn.microsoft.com/en-us/minecraft/creator/reference/content/particlesreference/particlecurves?view=minecraft-bedrock-stable"></a>
 */
public class SkinParticleCurve {

//     * <p>
// * <strong>linear</strong> is a series of nodes, equally spaced between 0 and 1 after applying input/horizontal_range.
// * <p>
// * <strong>bezier</strong> is a 4-node bezier spline, with the first and last point being the values at 0 and 1 and the middle two points forming the slope lines at 0.33 for the first point and 0.66 for the second.
//            * <p>
// * <strong>catmull_rom</strong> is a series of curves which pass through all but the last/first node.
//            * The first/last nodes are used to form the slope of the second/second-last points respectively.
// * All points are evenly spaced.
// * <p>
// * <strong>bezier_chain</strong> is a chain of bezier splines. A series of points are specified along with their corresponding slopes, and each segment uses its pair of points and slopes to form a bezier spline.
// * Each point aside from the first/last is shared between its pair of spline segments.
// * <p>
// * nodes are the control nodes for the curve. These are assumed to be equally spaced; this notation only works for linear, bezier, and catmull_rom.
//            * <p>
// * nodes for bezier chain are the control nodes for bezier_chain. The nodes will be sorted prior to parsing, so if you declare nodes 0.3, 0.6, 0.5, they will be re-ordered to 0.3, 0.5, 0.6.
//            * <p>
// * input <float/Molang> This is the input value to use. For example, variable.particle_age/variable.particle_lifetime results in an input from 0 to 1 over the lifetime of the particle, while variable.particle_age would provide input of how old the particle is in seconds.
//            * <p>

    public static SkinParticleCurve linear(OpenPrimitive input, OpenPrimitive range) {
        return new SkinParticleCurve();
    }

}

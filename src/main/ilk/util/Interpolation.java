package ilk.util;

import org.lazywizard.lazylib.FastTrig;

/** A set of interpolation methods for various things */
public class Interpolation {

  public static float clamp(float val, float min, float max) {
    if (val > max) {
      return max;
    } else if (val < min) {
      return min;
    } else {
      return val;
    }
  }

  public static float clamp(float val) {
    return clamp(val, 0f, 1f);
  }

  public static float linear(float val) {
    return clamp(val);
  }

  public static float sin(float val) {
    return (float) FastTrig.sin(Math.PI / 2. * clamp(val));
  }

  public static float inverseSin(float val) {
    return (float) FastTrig.cos(Math.PI * (1. + clamp(val) / 2.));
  }
}

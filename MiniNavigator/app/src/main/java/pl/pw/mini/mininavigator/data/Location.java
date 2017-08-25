package pl.pw.mini.mininavigator.data;

import java.io.Serializable;

public class Location implements Serializable{
    public double X;
    public double Y;
    public double Z;
    public int F;

    // Do not rely on data below in any way.
    public double SdXError;
    public double SdYError;
    public double SdZError;
    public double[] CondProbF;

    @Override
    public String toString() {

        return String.format("coords: [%.2f, %.2f, %.2f] floor: %d", X,Y,Z,F);
      //  return "[x:" + String.format() + " y:" + Y + " z:" + Z + "] floor:" + F;
    }
}

package pl.pw.mini.mininavigator.data;

import java.io.Serializable;

public class LocationDate implements Serializable{
    public Location Loc;
    public Date Dat;

    @Override
    public String toString() {
        return "[" + Dat + "] " + Loc;
    }
}

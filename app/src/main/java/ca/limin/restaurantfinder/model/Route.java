package ca.limin.restaurantfinder.model;

import java.util.ArrayList;
import java.util.List;

public class Route {
    private List<Leg> legs = new ArrayList<>();

    public List<Leg> getLegs() {
        return legs;
    }

    public void setLegs(List<Leg> legs) {
        this.legs = legs;
    }
}

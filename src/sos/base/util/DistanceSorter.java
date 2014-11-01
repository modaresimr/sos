package sos.base.util;

import java.util.Comparator;

import sos.base.entities.StandardEntity;
import sos.base.entities.StandardWorldModel;

/**
   A comparator that sorts entities by distance to a reference point.
*/
public class DistanceSorter implements Comparator<StandardEntity> {
    private StandardEntity reference;
    private StandardWorldModel world;

    /**
       Create a DistanceSorter.
       @param reference The reference point to measure distances from.
       @param world The world model.
    */
    public DistanceSorter(StandardEntity reference, StandardWorldModel world) {
        this.reference = reference;
        this.world = world;
    }

    @Override
    public int compare(StandardEntity a, StandardEntity b) {
        int d1 = world.getDistance(reference, a);
        int d2 = world.getDistance(reference, b);
        return d1 - d2;
    }
}

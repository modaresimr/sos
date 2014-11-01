package rescuecore2.standard.entities;

import rescuecore2.registry.AbstractEntityFactory;
import rescuecore2.worldmodel.EntityID;
import sos.base.entities.AmbulanceCenter;
import sos.base.entities.AmbulanceTeam;
import sos.base.entities.Blockade;
import sos.base.entities.Building;
import sos.base.entities.Civilian;
import sos.base.entities.FireBrigade;
import sos.base.entities.FireStation;
import sos.base.entities.GasStation;
import sos.base.entities.Hydrant;
import sos.base.entities.PoliceForce;
import sos.base.entities.PoliceOffice;
import sos.base.entities.Refuge;
import sos.base.entities.Road;
import sos.base.entities.StandardEntity;
import sos.base.entities.World;

/**
   EntityFactory that builds standard Robocup Standard objects.
 */
public final class StandardEntityFactory extends AbstractEntityFactory<StandardEntityURN> {
	/* ///////////////////S.O.S instants////////////////// */

	/* ////////////////////End of S.O.S/////////////////// */
	/* /////////////////////RESCUECORE//////////////////// */
    /**
       Singleton class. Use this instance to do stuff.
     */
    public static final StandardEntityFactory INSTANCE = new StandardEntityFactory();

    /**
       Singleton class: private constructor.
     */
    private StandardEntityFactory() {
        super(StandardEntityURN.class);
    }

    @Override
    public StandardEntity makeEntity(StandardEntityURN urn, EntityID id) {
        switch (urn) {
        case WORLD:
            return new World(id);
        case ROAD:
            return new Road(id);
        case BUILDING:
            return new Building(id);
        case BLOCKADE:
            return new Blockade(id);
        case REFUGE:
            return new Refuge(id);
        case FIRE_STATION:
            return new FireStation(id);
        case HYDRANT:
            return new Hydrant(id);
        case GAS_STATION:
            return new GasStation(id);
        case AMBULANCE_CENTRE:
            return new AmbulanceCenter(id);
        case POLICE_OFFICE:
            return new PoliceOffice(id);
        case CIVILIAN:
            return new Civilian(id);
        case FIRE_BRIGADE:
            return new FireBrigade(id);
        case AMBULANCE_TEAM:
            return new AmbulanceTeam(id);
        case POLICE_FORCE:
            return new PoliceForce(id);
        default:
            throw new IllegalArgumentException("Unrecognised entity urn: " + urn);
        }
    }
	/* //////////////End of RESCUECORE/////////////// */

	/* ///////////////////////S.O.S/////////////////////// */

	/* ////////////////////End of S.O.S/////////////////// */

}
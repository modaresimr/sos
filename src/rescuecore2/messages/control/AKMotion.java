package rescuecore2.messages.control;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import rescuecore2.messages.AbstractCommand;
import rescuecore2.messages.components.EntityIDListComponent;
import rescuecore2.messages.components.IntListComponent;
import rescuecore2.standard.messages.StandardMessageURN;
import rescuecore2.worldmodel.EntityID;

/**
+   An agent move command.
+ */
public class AKMotion extends AbstractCommand {
    private IntListComponent path;
    private EntityIDListComponent entityPath;

    /**
+       An AKMotion message that populates its data from a stream.
+       @param in The InputStream to read.
+       @throws IOException If there is a problem reading the stream.
+     */
    public AKMotion(InputStream in) throws IOException {
        this();
       read(in);
    }

    /**
+       Construct a move command.
+       @param time The time the command was issued.
+       @param agent The ID of the agent issuing the command.
+       @param path The path to move.
+     */
    public AKMotion(EntityID agent, int time, List<Integer> path, List<EntityID> entityPath) {
        this();
        setAgentID(agent);
        setTime(time);
       this.path.setValues(path);
        this.entityPath.setIDs(entityPath);
    }

    private AKMotion() {
        super(StandardMessageURN.AK_MOTION);
       path = new IntListComponent("Path");
        entityPath = new EntityIDListComponent("EntityPath");
        addMessageComponent(path);
        addMessageComponent(entityPath);
    }

    /**
       Get the desired movement path.
+       @return The movement path.
+     */
    public List<Integer> getPath() {
        return path.getValues();
    }

    public List<EntityID> getEntityPath() {
		return entityPath.getIDs();
	}
}
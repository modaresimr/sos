package rescuecore2.standard.components;

import rescuecore2.components.AbstractViewer;
import sample.SampleWorldModel;
import sos.base.entities.StandardWorldModel;

/**
   Abstract base class for standard viewers.
*/
public abstract class StandardViewer extends AbstractViewer<StandardWorldModel> {
    @Override
    protected StandardWorldModel createWorldModel() {
        return new SampleWorldModel(null);
    }

    @Override
	protected void postConnect() throws Exception {
        super.postConnect();
        model().index();
    }
}
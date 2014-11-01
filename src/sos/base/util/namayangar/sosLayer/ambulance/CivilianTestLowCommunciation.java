package sos.base.util.namayangar.sosLayer.ambulance;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.message.structure.MessageConstants.Type;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.search_v2.worldModel.SearchBuilding;

public class CivilianTestLowCommunciation extends SOSAbstractToolsLayer<Building>{

	public CivilianTestLowCommunciation() {
		super(Building.class);
	}
	@Override
	public int getZIndex() {
		return 30;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().buildings());
	}

	@Override
	protected Shape render(Building entity, Graphics2D g, ScreenTransform transform) {
		SearchBuilding sentity = model().searchWorldModel.getSearchBuilding(entity);
		if(sentity.isReallyUnReachableInLowCom(true))
			g.setColor(Color.red);
		else 
			g.setColor(Color.green);
		if(sentity.getValidCivilianCountInLowCom()!=0)
			NamayangarUtils.drawString("c:"+sentity.getValidCivilianCountInLowCom()+" ru:"+sentity.isReallyUnReachableInLowCom(true)+" t:"+sentity.getTimeInLowComValidCivilianCount(), g, transform, entity);
		return null;
	}

	@Override
	public JComponent getGUIComponent() {
		return null;
	}

	@Override
	public boolean isValid() {
		return model().sosAgent().messageSystem.type==Type.LowComunication;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(Building entity) {
		return null;
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Base;
	}
}

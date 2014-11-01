package sos.base.util.namayangar.sosLayer.search;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import rescuecore2.misc.Pair;
import sos.base.entities.Building;
import sos.base.util.namayangar.NamayangarUtils;
import sos.base.util.namayangar.misc.gui.ScreenTransform;
import sos.base.util.namayangar.sosLayer.other.SOSAbstractToolsLayer;
import sos.base.util.namayangar.tools.LayerType;
import sos.base.util.namayangar.tools.SOSSelectedObj;
import sos.base.util.namayangar.tools.SelectedObjectListener;
import sos.search_v2.tools.searchScore.AgentSearchScore;
import sos.search_v2.worldModel.SearchBuilding;

public class SearchScore extends SOSAbstractToolsLayer<sos.search_v2.worldModel.SearchBuilding> implements SelectedObjectListener {
	public SearchScore() {
		super(SearchBuilding.class);
		setVisible(false);
	}

	@Override
	public int getZIndex() {
		return 11;
	}

	@Override
	protected void makeEntities() {
		setEntities(model().searchWorldModel.getSearchBuildings());
	}

	@Override
	protected Shape render(SearchBuilding entity, Graphics2D g, ScreenTransform transform) {
		String lastState = "LastSearch= " + (model().sosAgent().newSearch.searchType);
		Font f = g.getFont();
		g.setFont(new Font("arial", Font.PLAIN, 30));
		g.setColor(Color.yellow);
		g.drawString(lastState, 30, 50);
		g.setFont(f);
		if (entity.tar) {
			g.setColor(Color.blue);
			Shape shape = NamayangarUtils.transformShape(entity.getRealBuilding(), transform);
			g.setStroke(new BasicStroke(4));
			g.draw(shape);
			g.setStroke(new BasicStroke(1));
		}
		g.setColor(Color.green);

		if(entity.getScore()<=AgentSearchScore.SEARCH_FILLTER_SCORE){
			g.setStroke(new BasicStroke(2));
			Shape shape = NamayangarUtils.transformShape(entity.getRealBuilding(), transform);
			g.setColor(Color.red);
//			g.drawString("", transform.xToScreen(entity.getRealBuilding().x()), transform.yToScreen(entity.getRealBuilding().y()));
			g.draw(shape);
			g.setStroke(new BasicStroke(1));
		}else if((int) entity.getScore()!=0)
			g.drawString((int) entity.getScore() + "", transform.xToScreen(entity.getRealBuilding().x()), transform.yToScreen(entity.getRealBuilding().y()));

		return null;
	}

	@Override
	public ArrayList<Pair<String, String>> sosInspect(SearchBuilding entity) {
		return null;
	}

	JTextArea textArea = new JTextArea();

	@Override
	public JComponent getGUIComponent() {
		return textArea;
	}

	@Override
	public boolean isValid() {
		return true;
	}

	private SearchBuilding selectedBuilding;
	@Override
	public void objectSelected(SOSSelectedObj sso) {
		if (sso == null) {
			selectedBuilding = null;
		} else {
			if (sso.getObject() instanceof Building) {
				selectedBuilding = (model()).searchWorldModel.getSearchBuilding((Building) sso.getObject());
				textArea.setText(selectedBuilding.reason+"\nfinalscore:"+(int)(model().searchWorldModel.getSearchBuilding((Building)sso.getObject())).getScore());
			} else
				selectedBuilding = null;
		}
	}

	@Override
	public void preCompute() {
		getViewer().getViewerFrame().addSelectedObjectListener(this);
		super.preCompute();
	}
	@Override
	public LayerType getLayerType() {
		return LayerType.Search;
	}
}

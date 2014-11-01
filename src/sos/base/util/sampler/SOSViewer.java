package sos.base.util.sampler;

import javax.swing.JOptionPane;

import rescuecore2.messages.control.KVTimestep;
import rescuecore2.standard.components.StandardViewer;

/**
 * @author Yoosef
 */
public class SOSViewer extends StandardViewer {

	public SOSViewer() throws SamplerException {
	}

	@Override
	protected void postConnect() {
		try {
			super.postConnect();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Connecting to SOS Namayangar have Error", "Error", 0);
		}

	}

	@Override
	protected void handleTimestep(final KVTimestep t) {
		super.handleTimestep(t);
	}

	@Override
	public String toString() {
		return "S.O.S Sampler";
	}

}

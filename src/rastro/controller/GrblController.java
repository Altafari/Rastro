package rastro.controller;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import rastro.comm.ISerialPort;
import rastro.model.GrblSettings.GrblSetting;
import rastro.model.ICoordListener;
import rastro.system.SystemManager;

public class GrblController {

	public enum Mode {
		INIT, PROGRAM, JOGGING
	};

	private Mode mode;
	private SystemManager sysMgr;
	private float[] origin;
	private Set<ICoordListener> originListeners;
	private final float DEFAULT_ORIGIN_X = 30.9f;
	private final float DEFAULT_ORIGIN_Y = 4.45f;

	private class ControlCommand implements ICommCommand {

		private String command;

		public ControlCommand(String cmdStr) {
			command = cmdStr;
		}

		@Override
		public boolean invoke(ISerialPort port) throws IOException {
			port.write(command.getBytes("US-ASCII"));
			StringBuilder sb = new StringBuilder();
			while (true) {
				byte[] buff = port.read(1);
				if (buff.length > 0) {
					sb.append((char) buff[0]);
					if (buff[0] == '\n') {
						if (sb.indexOf("ok") != -1) {
							return true;
						} else {
							return false;
						}
					}
				} else {
					return false;
				}
			}
		}
	}

	public GrblController(SystemManager sysManager) {
		sysMgr = sysManager;
		mode = Mode.INIT;
		origin = new float[3];
		origin[0] = DEFAULT_ORIGIN_X;
		origin[1] = DEFAULT_ORIGIN_Y;
		originListeners = new HashSet<ICoordListener>();
	}

	public void setOrigin() {
		if (!isIdleJogging()) {
			return;
		}
		origin = sysMgr.getGrblStatusMonitor().getPosition();
		notifyListeners();
	}

	public void homingCycle() {
		ICommCommand homingCmd = new ControlCommand("$H\n");
		sysMgr.getGrblCommController().sendCommand(homingCmd);
		mode = Mode.JOGGING;
	}

	public void goOrigin() {
		if (!isIdleJogging()) {
			return;
		}
		float xOffset = sysMgr.getFlipMirroringPanel().getFlipOffset();
		joggingMove(new float[] { origin[0] + xOffset, origin[1], origin[2] }, false);
	}

	public void joggingMove(float[] pos, boolean isRelative) {
		if (mode != Mode.JOGGING) {
			return;
		} else {
			int pBuffState = sysMgr.getGrblStatusMonitor().getPlannerBufferState();
			if (pBuffState > 1) {
				return;
			}
			moveCmd(pos, isRelative, 0.0f);
			sysMgr.getGrblStatusMonitor().startMonitoringTask();
		}
	}

	public void programMove(float[] pos, boolean isRelative, float feedRate) {
		if (mode != Mode.PROGRAM) {
			return;
		} else {
			moveCmd(pos, isRelative, feedRate);
		}
	}

	private void moveCmd(float[] pos, boolean isRelative, float feedRate) {
		String pref;
		if (isRelative) {
			pref = "G91";
		} else {
			pref = "G90";
		}
		if (feedRate == 0) {
			feedRate = sysMgr.getGrblSettings().getSettings().get(GrblSetting.MAX_RATE_X);
		}
		String cmdStr = String.format("%s G1 X%f Y%f F%f\n", pref, pos[0], pos[1], feedRate);
		ICommCommand moveCmd = new ControlCommand(cmdStr);
		sysMgr.getGrblCommController().sendCommand(moveCmd);
	}

	public void addOriginListener(ICoordListener l) {
		originListeners.add(l);
	}

	public void removeOriginListener(ICoordListener l) {
		originListeners.remove(l);
	}

	public void setMode(Mode m) {
		mode = m;
	}

	public float[] getOrigin() {
		return origin.clone();
	}

	public void forceNotification() {
		notifyListeners();
	}

	private void notifyListeners() {
		for (ICoordListener l : originListeners) {
			l.onChange(origin);
		}
	}

	private boolean isIdleJogging() {
		return mode == Mode.JOGGING && sysMgr.getGrblStatusMonitor().getPlannerBufferState() == 0;
	}
}

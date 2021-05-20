package rastro.comm.posix;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class Termios extends Structure {
	private static final int NCCS = 32;
	public int c_oflag;
	public int c_iflag;
	public int c_cflag;
	public int c_lflag;
	public byte c_line;
	public byte[] c_cc;
	public int c_ospeed;
	public int c_ispeed;

	public Termios() {
		c_cc = new byte[NCCS];
	}

	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(
				new String[] { "c_oflag", "c_iflag", "c_cflag", "c_lflag", "c_line", "c_cc", "c_ospeed", "c_ispeed" });
	}
}
package rastro.comm.posix;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

public class Dirent extends Structure {
	public NativeLong d_ino;
	public NativeLong d_off;
	public short d_reclen;
	public byte d_type;
	public byte[] d_name;

	public Dirent() {
		d_name = new byte[256];
	}
	
	@Override
	protected List<String> getFieldOrder() {
		return Arrays.asList(new String[] { "d_ino", "d_off", "d_reclen", "d_type", "d_name" });
	}
}
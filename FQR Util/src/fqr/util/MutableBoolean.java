package fqr.util;

public class MutableBoolean {

	private boolean val;
	
	public MutableBoolean() {
		val = false;
	}

	public boolean isTrue() {
		return val;
	}

	public void setTrue() {
		this.val = true;
	}

	public void setFalse() {
		this.val = false;
	}
}

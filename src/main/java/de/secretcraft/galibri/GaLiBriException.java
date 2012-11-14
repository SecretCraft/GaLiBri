package de.secretcraft.galibri;

public class GaLiBriException extends Exception {
	private static final long serialVersionUID = 938144334229031816L;

	public GaLiBriException() {
		super();
	}

	public GaLiBriException(String arg0) {
		super(arg0);
	}

	public GaLiBriException(Throwable arg0) {
		super(arg0);
	}

	public GaLiBriException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GaLiBriException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}
}

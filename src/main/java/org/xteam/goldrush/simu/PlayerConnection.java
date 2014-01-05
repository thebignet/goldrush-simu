package org.xteam.goldrush.simu;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

public interface PlayerConnection {

	void start() throws IOException;

	Reader getReader();

	Writer getWriter();

	void stop();
	
}

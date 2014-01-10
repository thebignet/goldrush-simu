package org.xteam.goldrush.simu.test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.xteam.goldrush.simu.PlayerConnection;

public class PlayerConnectionMock implements PlayerConnection {
	
	private StringReader reader;
	private StringWriter writer = new StringWriter();

	public PlayerConnectionMock(String commands) {
		reader = new StringReader(commands);
	}

	@Override
	public void start() throws IOException {
	}

	@Override
	public Reader getReader() {
		return reader;
	}

	@Override
	public Writer getWriter() {
		return writer;
	}

	public String getWritten() {
		return writer.toString();
	}

    @Override
    public void stop() {
    }
	
}
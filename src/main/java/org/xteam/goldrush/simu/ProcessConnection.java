package org.xteam.goldrush.simu;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class ProcessConnection implements PlayerConnection {

	private String[] commands;
	private Process process;

	public ProcessConnection(String playerExecutable) {
		commands = playerExecutable.split(" ");
	}

	@Override
	public void start() throws IOException {
		process = new ProcessBuilder(commands).redirectErrorStream(true).start();
	}

	@Override
	public Reader getReader() {
		return new InputStreamReader(process.getInputStream());
	}

	@Override
	public Writer getWriter() {
		return new OutputStreamWriter(process.getOutputStream());
	}

	@Override
	public void stop() {
		process.destroy();
	}
}

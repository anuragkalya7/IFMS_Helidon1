package com.pwc.helidon.ifms;

import java.io.IOException;

import io.helidon.microprofile.server.Server;

public class MainApplication {

	public MainApplication() {
	}

	public static void main(final String[] args) throws IOException {
		Server server = startServer();
		System.out.println("http://localhost:" + server.port());
	}

	static Server startServer() {
		return Server.create().start();
	}

}

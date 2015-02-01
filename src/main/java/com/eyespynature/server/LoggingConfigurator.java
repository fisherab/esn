package com.eyespynature.server;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

@Singleton
@Startup
public class LoggingConfigurator {

	@PostConstruct
	private void init() {
		/*
		 * This seems to be necessary even though the default initialisation is to load from the
		 * Classpath
		 */
		PropertyConfigurator.configure(LoggingConfigurator.class.getClassLoader().getResource(
				"log4j.properties"));

		Logger logger = Logger.getLogger(LoggingConfigurator.class);
		logger.info("Using log4j default configuration");
	}

}

package com.eyespynature.server.rest;

import java.io.ByteArrayOutputStream;

import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

@Provider
public class ThrowableExceptionMapper implements ExceptionMapper<Throwable> {

	private static Logger logger = Logger.getLogger(ThrowableExceptionMapper.class);

	@Override
	public Response toResponse(Throwable e) {
		logger.info("Processing: " + e.getClass() + " " + e.getMessage(), e);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JsonGenerator gen = Json.createGenerator(baos);
		gen.writeStartObject()
				.write("class", e.getClass().getName()).write("message",e.getMessage()).writeEnd().close();
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(baos.toString()).build();
	}
}
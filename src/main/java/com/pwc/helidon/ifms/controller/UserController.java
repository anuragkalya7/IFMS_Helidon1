
package com.pwc.helidon.ifms.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;

import com.google.gson.Gson;
import com.pwc.helidon.ifms.MPJWTToken;
import com.pwc.helidon.ifms.commons.IFMSException;
import com.pwc.helidon.ifms.model.UserDetails;
import com.pwc.helidon.ifms.service.UserService;

import io.helidon.config.Config;
import io.helidon.security.annotations.Authenticated;
import io.vertx.ext.auth.JWTOptions;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;

/**
 * 
 * 
 */

@Path("/user")
@ApplicationScoped
@OpenAPIDefinition(info = @Info(title = "User endpoint", version = "1.0"))
public class UserController {
	private static final JsonBuilderFactory JSON = Json.createBuilderFactory(Collections.emptyMap());

	private final UserService userService;

	@Inject
	Config config;

	@Inject
	public UserController(UserService userService, Config config) {
		this.userService = userService;
		this.config = config;
	}

	/**
	 * Return Auth token and Role details.
	 *
	 * @return {@link JsonObject}
	 */
	@GET
	@Path("/getToken")
	@Produces(MediaType.APPLICATION_JSON)
	@Authenticated
	public Response getAuthTokenDetails(@Context io.helidon.security.SecurityContext securityContext) {
		Gson gson = new Gson();
		try {
			String key = readPemFile();
			if (key == null) {
				throw new IFMSException("Unable to read private Key");
			}
			UserDetails userDetails = this.userService.getUserDetail(securityContext.userName());
			String token = generateJWT(key, userDetails, this.config);
			return Response.ok(JSON.createObjectBuilder()
					.add("userRoles", gson.toJson(userDetails.getRoleList()))
					.add("token", token).build()).build();
		} catch (IFMSException e) {
			return Response.status(Status.INTERNAL_SERVER_ERROR)
					.entity(JSON.createObjectBuilder().add("code", 500).add("message", e.getMessage()).build()).build();
		}
	}

	private static String generateJWT(String key, UserDetails userDetail, Config config) {

		JWTAuth provider = JWTAuth.create(null,
				new JWTAuthOptions().addPubSecKey(new PubSecKeyOptions().setAlgorithm("RS256").setSecretKey(key)));

		MPJWTToken token = new MPJWTToken();
		token.setAud(config.get("token.aud").asString().get());
		token.setIss(config.get("token.iss").asString().get());
		token.setJti(UUID.randomUUID().toString());
		token.setSub(config.get("token.sub").asString().get()); // Sub is required for WildFly Swarm
		token.setUpn(config.get("token.upn").asString().get());
		token.setIat(System.currentTimeMillis());
		token.setExp(System.currentTimeMillis() + 300000); // 3 minutes expiration!
		token.addAdditionalClaims("ssoid", userDetail.getSsoId());
		token.addAdditionalClaims("userType", userDetail.getUserType());
		token.addAdditionalClaims("userId", String.valueOf(userDetail.getUserId()));
		token.addAdditionalClaims("userRole", userDetail.getUserRole());

		//List<String> roleNameList = userDetail.getRoleList().stream().map(role -> role.getRoleName())
		//		.collect(Collectors.toList());
		token.setGroups(userDetail.getRoleList());

		return provider.generateToken(new io.vertx.core.json.JsonObject().mergeIn(token.toJSONString()),
				new JWTOptions().setAlgorithm("RS256"));
	}

	private static String readPemFile() {
		StringBuilder sb = new StringBuilder(8192);
		try (BufferedReader is = new BufferedReader(new InputStreamReader(
				UserController.class.getResourceAsStream("/privateKey.pem"), StandardCharsets.US_ASCII))) {
			String line;
			while ((line = is.readLine()) != null) {
				if (!line.startsWith("-")) {
					sb.append(line);
					sb.append('\n');
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}

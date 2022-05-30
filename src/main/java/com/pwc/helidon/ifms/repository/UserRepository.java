package com.pwc.helidon.ifms.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pwc.helidon.ifms.commons.IFMSException;
import com.pwc.helidon.ifms.model.UserDetails;

@Dependent
public class UserRepository {

	static Logger log = LoggerFactory.getLogger(UserRepository.class);

	@Inject
	@Named("ifmsDataSource")
	private DataSource testDataSource;

	public UserDetails getUserDetailsBySSOID(String ssoid) throws IFMSException {

		System.out.println("Fetch the User Details from Database using SSO ID - " + ssoid);
		log.info("User Repository - Fetch the User Details from Database using SSO ID " + ssoid);

		UserDetails userDetails = null;
		Connection connection = null;
		// StringJoiner roleList = new StringJoiner(",");
		List<String> roleList = new ArrayList<String>();
		try {
			connection = this.testDataSource.getConnection();

//			String sql = "SELECT * from USER_MASTER where SSO_ID = ?";
//			PreparedStatement ps = connection.prepareStatement(sql);
//			ps.setString(1, ssoid);
//			ResultSet rs = ps.executeQuery();
//			while (rs.next()) {
//				userDetails = new UserDetails(ssoid, rs.getLong("USER_ID"), rs.getString("USER_TYPE"),
//						rs.getString("USER_ROLE"));
//				JSONObject jsonObject = new JSONObject(userDetails.getUserRole());
//				JSONArray jsonArray = jsonObject.getJSONArray("roles");
//				for (int i = 0; i < jsonArray.length(); i++) {
//					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
//					roleList.add((String) jsonObject2.get("id"));
//				}
//			}
//			rs.close();
//			ps.close();
//
//			// Fetch the Role Names
//			List<RoleDetails> roleDetailsList = new ArrayList<RoleDetails>();
//			sql = "SELECT ROLE_ID, ROLE_NAME from ROLE_MASTER where ROLE_ID IN " + "(" + roleList + ")";
//			ps = connection.prepareStatement(sql);
//			rs = ps.executeQuery();
//			while (rs.next()) {
//				roleDetailsList.add(new RoleDetails(rs.getLong("ROLE_ID"), rs.getString("ROLE_NAME")));
//			}
//			
//			if(userDetails != null) {
//				userDetails.setRoleList(roleDetailsList);
//			}

			String sql = "SELECT JSON_OBJECT ( 'sso_id' VALUE usr.sso_id, 'user_id' VALUE usr.user_id, 'user_type' VALUE usr.user_type, 'roles' VALUE JSON_ARRAYAGG(JSON_OBJECT('name' value role.role_name)) ) AS data "
					+ "FROM mstr_user usr, JSON_TABLE ( user_role, '$.roles[*]' COLUMNS (role_id NUMBER PATH '$.id', is_active VARCHAR2 ( 1 CHAR ) PATH '$.is_active', eff_start_date TIMESTAMP PATH '$.eff_start_date', eff_end_date TIMESTAMP PATH '$.eff_end_date')) jt, "
					+ "mstr_role role "
					+ "WHERE usr.is_active = 'Y' AND usr.eff_start_dt <= sysdate AND ( usr.eff_end_dt IS NULL OR usr.eff_end_dt >= sysdate ) "
					+ "AND usr.sso_id = ? AND role.role_id = jt.role_id AND role.is_active = 'Y' AND role.eff_start_date <= sysdate "
					+ "AND ( role.eff_end_date IS NULL OR role.eff_end_date >= sysdate ) AND jt.is_active = 'Y' AND jt.eff_start_date <= sysdate "
					+ "AND ( jt.eff_end_date IS NULL OR jt.eff_end_date >= sysdate ) GROUP BY usr.sso_id, usr.user_id, usr.user_type ";
			PreparedStatement ps = connection.prepareStatement(sql);
			ps.setString(1, ssoid);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String jsonString = rs.getString("data");
				JSONObject jsonObject = new JSONObject(jsonString);
				JSONArray jsonArray = jsonObject.getJSONArray("roles");
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObject2 = jsonArray.getJSONObject(i);
					roleList.add((String) jsonObject2.get("name"));
				}
				userDetails = new UserDetails(ssoid, Integer.parseInt((String) jsonObject.get("user_id")),
						(String) jsonObject.get("user_type"), roleList);
				break;
			}

			rs.close();
			ps.close();

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("SQL Error - " + e.getMessage());
			log.info("User Repository -SQL Error: " + e.getMessage());
			throw new IFMSException("Database Error. Something went wrong");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error - " + e.getMessage());
			log.info("User Repository - Error: " + e.getMessage());
			throw new IFMSException("Error. Something went wrong");
		} finally {
			try {
				if (!connection.isClosed()) {
					connection.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		System.out.println("User Details - " + userDetails.toString());
		log.info("User Repository", "User Details - " + userDetails.toString());
		return userDetails;
	}
}

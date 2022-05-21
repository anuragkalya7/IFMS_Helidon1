package com.pwc.helidon.ifms.model;

import java.io.Serializable;
import java.util.List;

/*@Entity
@Table(name = "USER_MASTER")*/
public class UserDetails implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * @Id
	 * 
	 * @Column(name="SSO_ID")
	 */
	private String ssoId;

	/* @Column(name="USER_ID") */
	private long userId;

	/* @Column(name="USER_TYPE") */
	private String userType;

	/* @Column(name="USER_ROLE") */
	private String userRole;

	private List<String> roleList;

	// private List<RoleDetails> roleList;

	public UserDetails() {
		super();
	}

	/**
	 * @param ssoId
	 * @param userId
	 * @param userType
	 * @param roleList
	 */
	public UserDetails(String ssoId, long userId, String userType, List<String> roleList) {
		super();
		this.ssoId = ssoId;
		this.userId = userId;
		this.userType = userType;
		this.roleList = roleList;
	}

	/**
	 * @return the ssoId
	 */
	public String getSsoId() {
		return ssoId;
	}

	/**
	 * @param ssoId the ssoId to set
	 */
	public void setSsoId(String ssoId) {
		this.ssoId = ssoId;
	}

	/**
	 * @return the userId
	 */
	public long getUserId() {
		return userId;
	}

	/**
	 * @param userId the userId to set
	 */
	public void setUserId(long userId) {
		this.userId = userId;
	}

	/**
	 * @return the userType
	 */
	public String getUserType() {
		return userType;
	}

	/**
	 * @param userType the userType to set
	 */
	public void setUserType(String userType) {
		this.userType = userType;
	}

	/**
	 * @return the userRole
	 */
	public String getUserRole() {
		return userRole;
	}

	/**
	 * @param userRole the userRole to set
	 */
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	/**
	 * @return the roleList
	 */
	public List<String> getRoleList() {
		return roleList;
	}

	/**
	 * @param roleList the roleList to set
	 */
	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}

}

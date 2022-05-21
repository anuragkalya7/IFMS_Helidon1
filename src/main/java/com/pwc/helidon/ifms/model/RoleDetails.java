package com.pwc.helidon.ifms.model;

public class RoleDetails {

	private long roleId;
	private String roleName;

	/**
	 * @param roleId
	 * @param roleName
	 */
	public RoleDetails(long roleId, String roleName) {
		super();
		this.roleId = roleId;
		this.roleName = roleName;
	}

	/**
	 * 
	 */
	public RoleDetails() {
		super();
	}

	/**
	 * @return the roleId
	 */
	public long getRoleId() {
		return roleId;
	}

	/**
	 * @param roleId the roleId to set
	 */
	public void setRoleId(long roleId) {
		this.roleId = roleId;
	}

	/**
	 * @return the roleName
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * @param roleName the roleName to set
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@Override
	public String toString() {
		return "RoleDetails [roleId=" + roleId + ", roleName=" + roleName + "]";
	}

}

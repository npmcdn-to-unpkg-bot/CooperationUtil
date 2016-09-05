package pub.amitabha.util;

import java.io.Serializable;

import pub.amitabha.domain.User;

public class AuthorizationInfo implements Serializable {
	/**
	 * this object is serializable.
	 */
	private static final long serialVersionUID = 6624174512250637000L;

	private boolean signedIn;
	private long userId = -1;
	private int role;
	private String objStr = null;

	public AuthorizationInfo() {
		signedIn = false;
		userId = -1;
		objStr = null;
	}

	public static AuthorizationInfo fromString(String str) throws Exception {
		return (AuthorizationInfo) ObjectStringConverter.stringToObject(str);
	}

	public String getObjString() throws Exception {
		return objStr;
	}

	public String getSessionId() throws Exception {
		return User.getHash(objStr) + userId + System.currentTimeMillis();

	}

	public boolean isSignedIn() {
		return signedIn;
	}

	public void signIn(User user) throws Exception {
		this.signedIn = true;
		this.userId = user.getId();
		role = user.getRole();
		objStr = ObjectStringConverter.objectToString(this);
	}

	public void signOut() {
		this.signedIn = false;
		userId = -1;
		objStr = null;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long user) {
		this.userId = user;
	}

	public int getRole() {
		return role;
	}

}

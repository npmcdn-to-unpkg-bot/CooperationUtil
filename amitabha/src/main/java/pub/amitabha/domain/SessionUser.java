package pub.amitabha.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SessionUser {
	@Id
	private String sessionId;

	/**
	 * CHAR 或 VARCHAR 的最大长度可以到 255，TEXT最大长度 65535，MEDIUMTEXT最大长度
	 * 16777215，LONGTEXT最大长度 4294967295。
	 */
	@Column(columnDefinition = "TEXT")
	private String objString;

	private long creationTime;

	private long expiryTime;

	public SessionUser() {
		this.creationTime = System.currentTimeMillis();
	}

	public SessionUser(String sessionId, String objString, long expiryTime) {
		this.sessionId = sessionId;
		this.objString = objString;
		this.setExpiryTime(expiryTime);
		this.creationTime = System.currentTimeMillis();
	}

	public String getObjString() {
		return objString;
	}

	public void setObjString(String objString) {
		this.objString = objString;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}
}

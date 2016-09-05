package pub.amitabha.wechat.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import javax.persistence.Id;

@Entity
public class MyAuthorizationCode {
	private String wechatId;
	private String nonce;

	@Id
	@Column(name = "creationTime", unique = false, nullable = false)
	private long creationTime;

	@SuppressWarnings("unused")
	private MyAuthorizationCode() {
	}

	public MyAuthorizationCode(String wechatId, String nonce) {
		this.wechatId = wechatId;
		this.nonce = nonce;
		this.creationTime = System.currentTimeMillis();
	}

	public String getWechatId() {
		return wechatId;
	}

	public void setWechatId(String wechatId) {
		this.wechatId = wechatId;
	}

	public String getNonce() {
		return nonce;
	}

	public void setNonce(String nonce) {
		this.nonce = nonce;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}
}

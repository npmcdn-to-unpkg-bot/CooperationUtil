package pub.amitabha.wechat;

import java.io.Serializable;

public class WechatSetting implements Serializable {
	private static final long serialVersionUID = -925405969478818810L;
	public static final String TOKEN = "WECHAT_TOKEN";

	private String openId;
	private String token;
	private String encodingAesKey;
	private String appId;

	public WechatSetting() {
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String tOKEN) {
		token = tOKEN;
	}

	public String getEncodingAesKey() {
		return encodingAesKey;
	}

	public void setEncodingAesKey(String encodingAesKey) {
		this.encodingAesKey = encodingAesKey;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

}

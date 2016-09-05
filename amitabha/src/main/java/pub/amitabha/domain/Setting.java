package pub.amitabha.domain;

import java.io.Serializable;
import java.util.Hashtable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import pub.amitabha.util.ObjectStringConverter;

@Entity
public class Setting {
	public static final String GENERAL = "GENERAL";

	@Id
	private String settingId;

	/**
	 * Expect the setting detail is base on Hashtable.
	 */
	@Column(columnDefinition = "LongText")
	private String settingDetails;

	public String getSettingDetails() {
		return settingDetails;
	}

	public void setSettingDetails(String settingDetails) {
		this.settingDetails = settingDetails;
	}

	public void setSettingDetails(Serializable settingDetails) throws Exception {
		this.settingDetails = ObjectStringConverter.objectToString(settingDetails);
	}

	public Setting() {
	}

	public Setting(String settingId) {
		this.settingId = settingId;
		settingDetails = null;
	}
	
	public Setting(String settingId, String objStr) {
		this.settingId = settingId;
		settingDetails = objStr;
	}
	
	public Setting(String settingId, Hashtable<String, Serializable> obj) throws Exception {
		this.settingId = settingId;
		setSettingDetails(obj);
	}

	public Setting(String settingId, Serializable obj) throws Exception {
		this.settingId = settingId;
		setSettingDetails(obj);
	}

	public String getSettingId() {
		return settingId;
	}

	public void setSettingId(String settingId) {
		this.settingId = settingId;
	}

	public Hashtable<String, Serializable> getSetting() {
		if(settingDetails == null || settingDetails.equals(""))
			return null;

		@SuppressWarnings("unchecked")
		Hashtable<String, Serializable> obj = ObjectStringConverter.stringToObject(settingDetails, Hashtable.class);
		return obj;
	}

	public <T extends Serializable> T getSetting(Class<T> T) {
		if(settingDetails == null || settingDetails.equals(""))
			return null;

		T obj = ObjectStringConverter.stringToObject(settingDetails, T);
		return obj;
	}

}

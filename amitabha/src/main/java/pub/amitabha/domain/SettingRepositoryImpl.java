package pub.amitabha.domain;

import org.springframework.beans.factory.annotation.Autowired;

public class SettingRepositoryImpl {

	@Autowired
	SettingRepository repo;

	public Setting getGeneralSetting() {
		Setting setting = repo.findOne(Setting.GENERAL);
		if(setting == null)
			return new Setting(Setting.GENERAL);
		else
			return setting;
	}
}

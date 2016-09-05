package pub.amitabha.domain;

import org.springframework.data.repository.CrudRepository;

public interface SettingRepository extends CrudRepository<Setting, String>, SettingRepositoryCustom {
}

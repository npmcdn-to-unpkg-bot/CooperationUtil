package pub.amitabha.domain;

import org.springframework.data.repository.CrudRepository;

public interface DummyUserRepository extends CrudRepository<DummyUser, Long>, DummyUserRepositoryCustom {
	public DummyUser findByNickName(String nickName);
}

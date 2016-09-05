package pub.amitabha.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

	List<User> findByNickName(String nickName);

	User findByForeignId(String foreignId);

	User findByQqOpenId(String qqOpenId);

	User findByName(String name);
}

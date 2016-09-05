package pub.amitabha.wechat.domain;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface MyAuthorizationCodeRepository extends CrudRepository<MyAuthorizationCode, Long> {

	MyAuthorizationCode findByWechatIdAndNonce(String wechatId, String nonce);

	List<MyAuthorizationCode> findByCreationTimeLessThan(long creationTime);

	// You have to add @Transactional to avoid exception.
	// Add @Query to avoid hibernate to remove them one by one
	@Transactional
	@Modifying
	@Query("delete from MyAuthorizationCode where creationTime < :time")
	void deleteByCreationTimeLessThan(@Param("time") long creationTime);
}

package pub.amitabha.domain;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SessionUserRepository extends CrudRepository<SessionUser, String> {

	SessionUser findBySessionId(String sessionId);

	// You have to add @Transactional to avoid exception.
	// Add @Query to avoid hibernate to remove them one by one
	@Transactional
	@Modifying
	@Query("delete from SessionUser where (creationTime + expiryTime) < :time")
	void deleteExpiryRecords(@Param("time") long currentTime);
}

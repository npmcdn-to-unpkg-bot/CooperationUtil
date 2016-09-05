package pub.amitabha.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface PersonalRecordRepository extends CrudRepository<PersonalRecord, Long> {

	List<PersonalRecord> findByUserId(long userId);

	List<PersonalRecord> findByUserIdAndType(long userId, String type);

	PersonalRecord findByUserIdAndTypeAndRecordDate(long userId, String type, String recordDate);
}

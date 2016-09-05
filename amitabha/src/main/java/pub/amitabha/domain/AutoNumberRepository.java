package pub.amitabha.domain;

import org.springframework.data.repository.CrudRepository;

public interface AutoNumberRepository extends CrudRepository<AutoNumber, String> {

	AutoNumber findById(String id);
}

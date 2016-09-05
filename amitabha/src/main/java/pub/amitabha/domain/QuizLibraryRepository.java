package pub.amitabha.domain;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface QuizLibraryRepository extends CrudRepository<QuizLibrary, Long> {

	List<QuizLibrary> findByGroup(String group);
}

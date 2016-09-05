package pub.amitabha.domain;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * You have to initialize the repository when your application start up.
 * 
 * @author Ems
 *
 */
@Entity
public class AutoNumber {
	@Id
	private String id;
	private long number;
	private Timestamp timestamp;

	public AutoNumber() {
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Need to be injected when application launches, you cannot use Autowired
	 * here for static thing.
	 */
	public static AutoNumberRepository repo;

	public synchronized static long getNegativeId(String AnumId) {
		AutoNumber aNum;
		long id;
		if (!repo.exists(AnumId)) {
			id = -1;
			aNum = new AutoNumber(AnumId, id);
		} else {
			aNum = repo.findOne(AnumId);
			id = aNum.getNumber() - 1;
			aNum.setNumber(id);
		}
		repo.save(aNum);

		return id;
	}

	public AutoNumber(String id, long number) {
		this.id = id;
		this.number = number;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

}

package pub.amitabha.domain;

import java.sql.Timestamp;
import java.util.stream.Stream;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * For dummy user, the id starts from -1, then -2, -3...
 * 
 * @author Ems
 *
 */
@Entity
public class DummyUser {
	@Id
	private long id;

	/**
	 * Nick Name should be unique, ref to Repository.CreateByNickName;
	 */
	private String nickName;

	/**
	 * This is a User.id list, separated by comma, starts and ends with a comma.
	 * E.G.(",1,2,3,") Because you need to SQL it with "LIKE '%,:id,%'"
	 */
	private String maintainedBy;

	private Timestamp timestamp;

	private static final String AnumId = DummyUser.class.getSimpleName();

	/**
	 * Please always use this method to new an object.
	 * 
	 * @return
	 */
	public static DummyUser create() {
		DummyUser du = new DummyUser();
		du.id = AutoNumber.getNegativeId(AnumId);
		return du;
	}

	/**
	 * Please do not use this constructor but use the static create method.
	 */
	DummyUser() {
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Never create by nick name, nick name is unique.
	 * 
	 * @param nickName
	 */
	@SuppressWarnings("unused")
	private DummyUser(String nickName) {
	}

	public long getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getMaintainedBy() {
		return maintainedBy;
	}

	public void addMaintainedBy(long userId) {
		if (maintainedBy != null && maintainedBy.contains("," + userId + ",")) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		if (maintainedBy == null || maintainedBy.equals("")) {
			sb.append(",");
		} else {
			sb.append(maintainedBy);
		}
		sb.append(userId).append(",");
		maintainedBy = sb.toString();
	}

	public void removeMaintainedBy(long userId) {
		String[] users = this.maintainedBy.split(",");
		String[] userList = Stream.of(users).parallel().filter(user -> user != String.valueOf(userId))
				.toArray(size -> new String[size]);

		String result = String.join(",", userList);
		if (result.equals(""))
			this.maintainedBy = result;
		else
			this.maintainedBy = "," + this.maintainedBy + ",";
	}

	public void setMaintainedBy(String maintainedBy) {
		this.maintainedBy = maintainedBy;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
}

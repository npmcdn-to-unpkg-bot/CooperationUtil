package pub.amitabha.domain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
public class User implements Serializable {

	private static final long serialVersionUID = -4232623664120815632L;

	public static final int ROLE_PENDING_APPROVAL = 0;
	public static final int ROLE_IN_FORCE = 1;
	public static final int ROLE_ADMIN = 2;
	public static final int ROLE_SUPER_ADMIN = 9;
	public static final int ROLE_INVALID = -1;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// Foreign ID is the open id in Wechat
	protected String foreignId;
	// qqOpenId is Oauth of QQ
	protected String qqOpenId;

	@Size(min = 2, max = 30)
	@NotNull
	protected String name;

	@NotNull
	@Size(min = 6, max = 100)
	protected String password;

	@Size(max = 30)
	protected String realName;

	@Size(max = 30)
	protected String nickName;

	protected int role = ROLE_PENDING_APPROVAL;

	protected Timestamp timestamp;

	public static User createUser() {
		return new User();
	}

	protected User() {
		timestamp = new Timestamp(System.currentTimeMillis());
	}

	public User(String name, String password) {
		this.setName(name);
		this.password = password;
		timestamp = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Check if current user can be edit/remove by some user role.
	 * 
	 * @param role
	 * @return
	 */
	public boolean canBeAdminBy(int role) {
		return (this.role < role) && (role >= ROLE_ADMIN);
	}

	public boolean canBeAdminBy(User user) {
		if (user.getId() == this.id)
			return true;
		else
			return (this.role < user.getRole()) && (user.getRole() >= ROLE_ADMIN);
	}

	/**
	 * Clone User properties
	 * 
	 * @param newUser
	 * @return
	 */
	public static User cloneUser(User newUser) {
		User user = new User(newUser.getName(), newUser.getPassword());
		user.UpdateExceptPassword(newUser);
		return user;
	}

	public User cloneUser() {
		return cloneUser(this);
	}

	public void copyExceptPassword(User user) {
		this.id = user.id;
		this.name = user.getName();
		UpdateExceptPassword(user);
	}

	public void UpdateExceptPassword(User user) {
		this.foreignId = user.foreignId;
		this.qqOpenId = user.qqOpenId;
		this.role = user.role;
		this.nickName = user.nickName;
		this.realName = user.realName;
	}

	public void UpdatePassword(User user) {
		this.password = user.password;
	}

	/**
	 * Calls the getter methods to get the field by field name.
	 * 
	 * @param fieldName
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public Object getValueByField(String fieldName) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {
		StringBuilder sb = new StringBuilder("get");
		sb.append(fieldName.substring(0, 1).toUpperCase()).append(fieldName.substring(1));

		Method m = this.getClass().getDeclaredMethod(sb.toString());
		return m.invoke(this);
	}

	@Override
	public String toString() {
		return String.format("Customer[id=%d, Nick Name='%s', name='%s']", id, nickName, password);
	}

	static final char[] hexDigits = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getHash(String pass) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(pass.getBytes());
			byte[] bytes = md5.digest();

			int j = bytes.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = bytes[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void encryptPassword() {
		this.password = getHash(this.password);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getForeignId() {
		return foreignId;
	}

	public void setForeignId(String foreignId) {
		this.foreignId = foreignId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getQqOpenId() {
		return qqOpenId;
	}

	public void setQqOpenId(String qqOpenId) {
		this.qqOpenId = qqOpenId;
	}
}

package pub.amitabha.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserLoginForm {

	@NotNull
	@Size(min = 3, max = 100)
	private String userName;

	@NotNull
	@Size(min = 3, max = 100)
	private String password;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}

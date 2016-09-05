package pub.amitabha.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UserForm extends User {
	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 6, max = 100)
	private String confirmPassword;

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

}

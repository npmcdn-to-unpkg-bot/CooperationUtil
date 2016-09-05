package pub.amitabha.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class should not be a entity. It's part of the table Project.
 * @author Ems
 *
 */
public class ProjectItem implements Serializable {
	private static final long serialVersionUID = 8004190750797610994L;

	public static final int TYPE_AMOUNT = 0;
	public static final int TYPE_TASK = 1;
	public static final int TYPE_COMPLETED = 1;
	public static final int TYPE_PENDING = 0;
	private String name;
	private int type;
	private boolean mandatory;
	private String Description;
	private Timestamp timestamp;

	public ProjectItem() {
		this.setTimestamp(new Timestamp(System.currentTimeMillis()));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}

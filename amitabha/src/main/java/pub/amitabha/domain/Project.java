package pub.amitabha.domain;

import java.sql.Timestamp;
import java.util.Hashtable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import pub.amitabha.util.ObjectStringConverter;

@Entity
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	// User ID
	@NotNull
	protected long charman;

	@NotNull
	@Size(min = 3, max = 50)
	protected String name;

	@Column(columnDefinition = "LongText")
	protected String itemNameStr;

	// User Id
	protected long createdBy;

	protected Timestamp timestamp;

	public Project() {
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	public Project(long chairman) {
		this.setCharman(chairman);
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	@SuppressWarnings("unchecked")
	public Hashtable<String, ProjectItem> getItemNames() {
		return ObjectStringConverter.stringToObject(itemNameStr, Hashtable.class);
	}

	public void setItemNames(Hashtable<String, ProjectItem> ht) {
		try {
			itemNameStr = ObjectStringConverter.objectToString(ht);
		} catch(Exception e) {
			System.out.println("DEBUG: failed to convert ht to itemNameStr " + e.getMessage());
		}
	}

	public String getItemNameStr() {
		return itemNameStr;
	}

	public void setItemNameStr(String itemNameStr) {
		this.itemNameStr = itemNameStr;
	}
	
	public long getCharman() {
		return charman;
	}

	public void setCharman(long charman) {
		this.charman = charman;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}

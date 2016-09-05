package pub.amitabha.domain;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import pub.amitabha.util.DateToTimestamp;

@Entity
public class PersonalRecord {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private long userId;

	private String recordDate;

	private String type;
	private int amount;

	private Timestamp timestamp;

	@SuppressWarnings("unused")
	private PersonalRecord() {
	}

	public PersonalRecord(long userId, String type, int amount) {
		this.userId = userId;
		this.type = type;
		this.amount = amount;
		this.recordDate = DateToTimestamp.getDate(new Date());
		timestamp = new Timestamp(System.currentTimeMillis());
	}

	public PersonalRecord(long userId, String type, int amount, Date date) {
		this.userId = userId;
		this.type = type;
		this.amount = amount;
		this.recordDate = DateToTimestamp.getDate(date);
		timestamp = new Timestamp(System.currentTimeMillis());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(String recordDate) {
		this.recordDate = recordDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
}

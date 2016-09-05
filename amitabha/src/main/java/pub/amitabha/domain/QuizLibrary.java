package pub.amitabha.domain;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class QuizLibrary {
	public static final int TYPE_SINGLE_CHOICE = 0;
	public static final int TYPE_MULTI_CHOICE = 1;
	public static final int TYPE_TRUE_FALSE = 2;
	public static final int TYPE_FILL_BLANK = 3;
	public static final int TYPE_BRIEF_ANSWER = 4;
	public static final int TYPE_ARTICLE = 5;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	/**
	 * Check the constant of this class starts with TYPE_*;
	 */
	private int type;
	
	/**
	 * Group can be used to facilitate the quiz paper. Group as easy/hard/harder etc..
	 */
	private String group;

	@Column(columnDefinition = "TEXT")
	private String question;

	/**
	 * Only choice, true/false and fill-blank questions got an answer.
	 */
	@Column(columnDefinition = "VARCHAR")
	private String answer;

	private Timestamp timestamp;

	public QuizLibrary() {
		setTimestamp(new Timestamp(System.currentTimeMillis()));
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}	
}

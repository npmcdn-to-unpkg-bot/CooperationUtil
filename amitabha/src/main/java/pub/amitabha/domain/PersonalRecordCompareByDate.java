package pub.amitabha.domain;

import java.util.Comparator;

public class PersonalRecordCompareByDate implements Comparator<PersonalRecord> {
	public int compare(PersonalRecord p1, PersonalRecord p2) {
		return p1.getRecordDate().compareTo(p2.getRecordDate());
	}

}

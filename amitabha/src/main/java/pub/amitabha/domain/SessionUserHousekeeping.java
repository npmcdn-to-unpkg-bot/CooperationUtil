package pub.amitabha.domain;

public class SessionUserHousekeeping implements Runnable {
	public static final long HOUSEKEEP_FREQUENCY = 2 * 60 * 60 * 1000;

	private SessionUserRepository repo;

	public SessionUserHousekeeping(SessionUserRepository repo) {
		this.repo = repo;
	}

	public void run() {
		long current = System.currentTimeMillis();
		System.out.println("**** Doing housekeeping: " + this.getClass());

		// repo.delete(repo.findByCreationTimeLessThan(current -
		// MILLISECONDS_TO_KEEP));
		repo.deleteExpiryRecords(current);
	}
}

package pub.amitabha.wechat.domain;

/**
 * Remove all the MyAuthorizationCode records with creation code less then
 * MILLISECONDS_TO_KEEP
 * 
 * @author Ems
 *
 */
public class MyAuthorizationCodeHousekeeping implements Runnable {
	public static final long MILLISECONDS_TO_KEEP = 2 * 60 * 60 * 1000;

	private MyAuthorizationCodeRepository repo;

	public MyAuthorizationCodeHousekeeping(MyAuthorizationCodeRepository repo) {
		this.repo = repo;
	}

	public void run() {
		long current = System.currentTimeMillis();
		System.out.println("**** Doing housekeeping: " + this.getClass());

		// repo.delete(repo.findByCreationTimeLessThan(current -
		// MILLISECONDS_TO_KEEP));
		repo.deleteByCreationTimeLessThan(current - MILLISECONDS_TO_KEEP);
	}
}

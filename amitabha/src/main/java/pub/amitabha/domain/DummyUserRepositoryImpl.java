package pub.amitabha.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

public class DummyUserRepositoryImpl {
	@PersistenceContext
	private EntityManager em;

	public List<DummyUser> findByMaintainedUser(long userId) {
		String hql = "select d from " + DummyUser.class.getSimpleName() + " d where d.maintainedBy like '%," + userId
				+ ",%'";
		TypedQuery<DummyUser> q = em.createQuery(hql, DummyUser.class);
		return q.getResultList();
	}

	@Autowired
	DummyUserRepository repo;

	public DummyUser createByNickName(String nickName, long maintainedBy) {
		DummyUser du = repo.findByNickName(nickName);
		if (du != null) {
			du.addMaintainedBy(maintainedBy);
		} else {
			du = DummyUser.create();
			du.setNickName(nickName);
			du.addMaintainedBy(maintainedBy);
		}
		return du;
	}
}

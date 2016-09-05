package pub.amitabha.domain;

import java.util.List;

public interface DummyUserRepositoryCustom {

	public List<DummyUser> findByMaintainedUser(long userId);

	public DummyUser createByNickName(String nickName, long maintainedBy);
}

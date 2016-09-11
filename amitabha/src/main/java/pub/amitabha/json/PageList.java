package pub.amitabha.json;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class PageList {
	private long totalPage;
	private long currentPage;

	private List<Map.Entry<String, Serializable>> list;

	public PageList() {
		//Do not use Collections.emptyList(); It may cause some problem like "UnsupportedOperationException" 
		list = new java.util.LinkedList<>();
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}

	public List<Map.Entry<String, Serializable>> getList() {
		return list;
	}

	public void push(String key, Serializable value) {
		Map.Entry<String, Serializable> entry = new Pair<>(key, value);
		list.add(entry);
	}

	public void setList(Map<String, Serializable> mapList) {
		mapList.keySet().parallelStream().forEach(key -> {
			push(key, mapList.get(key));
		});
	}

	public void setList(List<Map.Entry<String, Serializable>> list) {
		this.list = list;
	}
}

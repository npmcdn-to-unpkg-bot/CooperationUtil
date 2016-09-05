package pub.amitabha.util;

import java.util.Spliterator;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;

public class Page {
	public static int pageSize = 4;

	private int currentPage;
	private int totalPage;

	public Page(int page, int pageCount) {
		this.currentPage = page;
		this.totalPage = pageCount;
	}

	public static <T> Object[] paging(HttpServletRequest request, Model model, Iterable<T> iter) {
		Spliterator<T> spliterator = iter.spliterator();

		String pageStr = request.getParameter("page");
		int page;
		if (pageStr == null) {
			page = 1;
		} else {
			try {
				page = Integer.valueOf(pageStr);

				if (page < 1) {
					page = 1;
				}
			} catch (Exception e) {
				page = 1;
			}
		}
		long size = spliterator.estimateSize();

		int pageCount = (int) (size / pageSize);
		if (size % pageSize != 0)
			pageCount++;
		if (page > pageCount)
			page = pageCount;

		model.addAttribute("page", new Page(page, pageCount));

		return StreamSupport.stream(spliterator, true).skip((page - 1) * pageSize).limit(pageSize).toArray();
	}

	public boolean hasPrev() {
		return currentPage > 1;
	}

	public boolean hasNext() {
		return currentPage < totalPage;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int nextPage() {
		return currentPage + 1;
	}

	public int prevPage() {
		return currentPage - 1;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
}

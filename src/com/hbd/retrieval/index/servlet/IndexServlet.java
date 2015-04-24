package com.hbd.retrieval.index.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hbd.retrieval.index.manager.IndexerManager;

public class IndexServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//String imgPath = getServletContext().getInitParameter("imgPath");
		String imgDataPath = (String) this.getServletContext().getAttribute("ImgDataPath");
		String imgPath = imgDataPath + this.getServletContext().getInitParameter("imgPath");

		String indexPath = this.getServletContext().getInitParameter("indexPath");
		try {
			IndexerManager.getInstance().createIndex(imgPath, imgDataPath + indexPath);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		response.sendRedirect(request.getContextPath() + "/home.jsp");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}
}

package com.hbd.retrieval.search.servlet;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.hbd.retrieval.common.domain.DocBuilderConfig;
import com.hbd.retrieval.common.util.CommonUtils;
import com.hbd.retrieval.common.util.ConfigManager;
import com.hbd.retrieval.search.manager.SearcherManager;


public class SearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 用于存放用来检索的图片
	private File uploadPath;
		
	// 用于存放临时文件的目录
	private File tempPath;
		
	
	@Override
	public void init() throws ServletException {
		super.init();
		
		uploadPath = new File(getServletContext().getInitParameter("uploadPath"));
		System.out.println("uploadPath=====" + uploadPath);
		//如果目录不存在
		if (!uploadPath.exists()) {
			//创建目录
			uploadPath.mkdir();
		}
		//临时目录
		tempPath = new File(getServletContext().getInitParameter("tempPath"));
		if (!tempPath.exists()) {
			tempPath.mkdir();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		File file;
		int maxFileSize = 5000 * 1024;
		int maxMemSize = 5000 * 1024;
		// 验证上传内容类型
		String contentType = request.getContentType();
		if ((contentType.indexOf("multipart/form-data") >= 0)) {

			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 设置内存中的存储文件的最大值
			factory.setSizeThreshold(maxMemSize);

			factory.setRepository(tempPath);

			// 创建一个新的文件上传处理程序
			ServletFileUpload upload = new ServletFileUpload(factory);

			// 设置最大上传的文件大小
			upload.setSizeMax(maxFileSize);
			try {
				// 解析获取的文件
				List<FileItem> fileItems = upload.parseRequest(request);

				// 处理上传的文件
				Iterator<FileItem> iterator = fileItems.iterator();

				while (iterator.hasNext()) {
					FileItem item = (FileItem) iterator.next();
					if (!item.isFormField()) {
						String fileName = item.getName();
						// 写入文件
						if (fileName.lastIndexOf("\\") >= 0) {
							file = new File(uploadPath,
									fileName.substring(fileName
											.lastIndexOf("\\")));
						} else {
							file = new File(uploadPath,
									fileName.substring(fileName
											.lastIndexOf("\\") + 1));
						}
						item.write(file);
						System.out.println(file.getAbsolutePath());
						String[] results  = getResult(file);
						request.setAttribute("src", file.getAbsolutePath());
						request.setAttribute("result", results);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		request.getRequestDispatcher("/page_1.jsp").forward(request, response);
	}

	private String[] getResult(File file){
		List<String[]> resultList = new ArrayList<String[]>();

		List<DocBuilderConfig> configList = ConfigManager.getConfigList();
		for(Iterator<DocBuilderConfig> iterator = configList.iterator(); iterator.hasNext();){
			DocBuilderConfig builderConfig = iterator.next();
			String builderName = builderConfig.getBuilderName();
			String builderFolderName = builderConfig.getIndexFolderName();
			
			String[] result = SearcherManager.getInstance().getSearcherResult(file.getAbsolutePath(), builderName, builderFolderName);
			resultList.add(result);
		}
		String finalResult[] = CommonUtils.getSameSearchResult(resultList);
		System.out.println("-----检索结果如下-----");
		for(int i=0; i<finalResult.length; i++){
			System.out.println(finalResult[i]);
		}
		System.out.println("---------------");
		return finalResult;
	}
}

package com.hbd.retrieval.search.servlet;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.omg.CORBA.portable.ApplicationException;

import com.hbd.retrieval.common.domain.DocBuilderConfig;
import com.hbd.retrieval.common.util.CommonUtils;
import com.hbd.retrieval.common.util.ConfigManager;
import com.hbd.retrieval.common.util.UniqueMap;
import com.hbd.retrieval.search.manager.SearcherManager;


public class SearchServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// ���ڴ������������ͼƬ
	private File uploadImg;
		
	// ���ڴ����ʱ�ļ���Ŀ¼
	private File tempImg;
		
	private String imgDataPath;
	
	private String indexPath;
	
	private String projectName;
	
	private String uploadPath;
	
	private String tempPath;
	@Override
	public void init() throws ServletException {
		super.init();
		projectName = this.getServletContext().getContextPath();
		imgDataPath = (String) this.getServletContext().getAttribute("ImgDataPath");
		indexPath = this.getServletContext().getInitParameter("indexPath");
		uploadPath = imgDataPath + this.getServletContext().getInitParameter("uploadPath");
		tempPath = imgDataPath + this.getServletContext().getInitParameter("tempPath");
		uploadImg = new File(uploadPath);
		System.out.println("uploadPath=====" + uploadPath);
		//���Ŀ¼������
		if (!uploadImg.exists()) {
			//����Ŀ¼
			uploadImg.mkdir();
		}
		//��ʱĿ¼
		tempImg = new File(tempPath);
		if (!tempImg.exists()) {
			tempImg.mkdir();
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		File file;
		int maxFileSize = 5000 * 1024;
		int maxMemSize = 5000 * 1024;
		// ��֤�ϴ���������
		String contentType = request.getContentType();
		if ((contentType.indexOf("multipart/form-data") >= 0)) {

			DiskFileItemFactory factory = new DiskFileItemFactory();
			// �����ڴ��еĴ洢�ļ������ֵ
			factory.setSizeThreshold(maxMemSize);

			factory.setRepository(tempImg);

			// ����һ���µ��ļ��ϴ��������
			ServletFileUpload upload = new ServletFileUpload(factory);

			// ��������ϴ����ļ���С
			upload.setSizeMax(maxFileSize);
			try {
				// ������ȡ���ļ�
				List<FileItem> fileItems = upload.parseRequest(request);

				// �����ϴ����ļ�
				Iterator<FileItem> iterator = fileItems.iterator();

				while (iterator.hasNext()) {
					FileItem item = (FileItem) iterator.next();
					if (!item.isFormField()) {
						String fileName = item.getName();
						// д���ļ�
						if (fileName.lastIndexOf("\\") >= 0) {
							file = new File(uploadImg,
									fileName.substring(fileName
											.lastIndexOf("\\")));
						} else {
							file = new File(uploadImg,
									fileName.substring(fileName
											.lastIndexOf("\\") + 1));
						}
						item.write(file);
						File newFile = CommonUtils.renameFile(file, uploadPath);
						if(newFile != null){
							System.out.println(newFile.getAbsolutePath());
							String[] results  = getResult(newFile);
							String srcPath = newFile.getAbsolutePath();
							srcPath = srcPath.replaceAll("\\\\", "/");
							srcPath = srcPath.substring(srcPath.indexOf(projectName) + projectName.length());
							request.setAttribute("srcPath", srcPath);
							request.setAttribute("results", results);
						}else{
							throw new ApplicationException("Write file error", null);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		request.getRequestDispatcher("/page_1.jsp").forward(request, response);
	}

	private String[] getResult(File file) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
	
		Map<Float,String> tempMap = new HashMap<Float, String>();
		ImageSearchHits hits = null;
		List<DocBuilderConfig> configList = ConfigManager.getConfigList();
		for(Iterator<DocBuilderConfig> iterator = configList.iterator(); iterator.hasNext();){
			DocBuilderConfig builderConfig = iterator.next();
			String builderName = builderConfig.getBuilderName();
			Float weight = builderConfig.getWeight();
			hits = SearcherManager.getInstance().getSearcherResult(file.getAbsolutePath(), builderName, imgDataPath + indexPath);
			float sum = 0f;
			//��һ��
			for (int i = 0; i < hits.length() && i < 5; i++) {
				sum = sum + hits.score(i);
			}
			for (int i = 0; i < hits.length() && i < 5; i++) {
				String fileName = hits.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue();
				// System.out.println(i+":CEDD  "+hits1.score(i)/sum1+":"+hits1.doc(i).getField(DocumentBuilder.FIELD_NAME_IDENTIFIER).stringValue());
				tempMap.put(weight * hits.score(i) / sum, fileName);
			}
		}
		
		Map<Float,String> resultMap = UniqueMap.removeRepetitionFromMap(tempMap);
		resultMap = UniqueMap.transferToSortedMap(resultMap);
		UniqueMap.printMap(resultMap);
		String[] results = new String[resultMap.size()];
		results = UniqueMap.getResultsFromMap(resultMap);
		results = CommonUtils.getRelativePath(results, projectName);
		return results;
	}
}

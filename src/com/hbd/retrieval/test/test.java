package com.hbd.retrieval.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.hbd.retrieval.common.domain.DocBuilderConfig;
import com.hbd.retrieval.common.util.ConfigManager;
import com.hbd.retrieval.search.manager.SearcherManager;


public class test {

	public static void main(String[] args) {
		
		List<String[]> resultList = new ArrayList<String[]>();
		
		File directory = new File("");//设定为当前文件夹
		String currentPath = directory.getAbsolutePath();
		System.out.println(currentPath);
		List<DocBuilderConfig> configList = ConfigManager.getConfigList();
		for(Iterator<DocBuilderConfig> iterator = configList.iterator(); iterator.hasNext();){
			DocBuilderConfig builderConfig = iterator.next();
			String builderName = builderConfig.getBuilderName();
			String builderFolderName = builderConfig.getIndexFolderName();
			//IndexerManager.getInstance().createIndex(currentPath + "\\img", builderName, builderFolderName);
			
			String[] result = SearcherManager.getInstance().getSearcherResult(currentPath + "\\Image 026.jpg", builderName, builderFolderName);
			resultList.add(result);
		}
		/*String finalResult[] = CommonUtils.getSameSearchResult(resultList);
		System.out.println("-----检索结果如下-----");
		for(int i=0; i<finalResult.length; i++){
			System.out.println(finalResult[i]);
		}*/
		System.out.println("---------------");
	}

}

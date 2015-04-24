package com.hbd.retrieval.common.util;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonUtils {
	
	/**
	 * 获取List<String[]>中各个字符串数组的相同元素的数组
	 * @param resultList
	 * @return
	 */
	public static String[] getSameSearchResult(List<String[]> resultList){
		String[] sameElements = resultList.get(0);
		for(int i=1; i<resultList.size(); i++){
			sameElements = findSameElementIn2Arrays(sameElements, resultList.get(i));
		}
		return sameElements;
	}
	
	/**
	 * 找出两个相同数组的相同元素
	 * @param strArray1
	 * @param strArray2
	 * @return
	 */
	public static String[] findSameElementIn2Arrays(String[] strArray1, String[] strArray2){
		Set<String> sameSet = new HashSet<String>();//存放数组的相同元素
		Set<String> tempSet = new HashSet<String>();//存放数组1中的元素
		
		for(int i=0; i<strArray1.length; i++){
			tempSet.add(strArray1[i]);
		}
		
		for(int i=0; i<strArray2.length; i++){
			//把数组2中的元素添加到tempSet中
		    //如果tempSet中已存在相同的元素，则tempSet.add（array2[j]）返回false
			if(!tempSet.add(strArray2[i])){
				sameSet.add(strArray2[i]);
			}
		}
		
		Object[] objects = sameSet.toArray();
		String[] sameElements = new String[objects.length];
		for(int i=0; i<objects.length; i++){
			sameElements[i] = objects[i].toString();
		}
		return sameElements;
	}
	
	/**
	 * 删除文件
	 * @param file
	 */
	public static void delelteFile(File file){	
		if (file != null) {
            if (file.isFile()) {
                System.out.println(file.getPath());
                file.delete();
            } else if (file.isDirectory()) {
                System.out.println(file.getPath());
                File[] subFiles = file.listFiles();
                int n = subFiles.length;
                for (int i = 0; i < n; i++) {
                	delelteFile(subFiles[i]);
                }
                file.delete();
            }
        }
	}
	
	/**
	 * 文件重命名
	 * @param file文件
	 * @param prefixStr文件目录前缀
	 * @return
	 */
	public static File renameFile(File file, String prefixStr){
		Format format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String newName = format.format(new Date());
		File newFile = new File(prefixStr + newName + ".jpg");
		if(file.renameTo(newFile)){
			return newFile;
		}else{
			return null; 
		}
		
	}
	
	/**
	 * 获取图片在服务器的相对路径
	 * @param results
	 * @param projectName
	 * @return
	 */
	public static String[] getRelativePath(String[] results, String projectName){
		String[] relativePaths = new String[results.length];
		for(int i = 0; i < results.length; i++){
			results[i] = results[i].replaceAll("\\\\", "/");
			results[i] = results[i].substring(results[i].indexOf(projectName) + projectName.length());
			relativePaths[i] = results[i];
		}
		return relativePaths;
	}
}

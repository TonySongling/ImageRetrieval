package com.hbd.retrieval.common.util;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommonUtils {
	
	public static String[] getSameSearchResult(List<String[]> resultList){
		String[] sameElements = resultList.get(0);
		for(int i=1; i<resultList.size(); i++){
			sameElements = findSameElementIn2Arrays(sameElements, resultList.get(i));
		}
		return sameElements;
	}
	
	public static String[] findSameElementIn2Arrays(String[] strArray1, String[] strArray2){
		Set<String> sameSet = new HashSet<String>();//����������ͬԪ��
		Set<String> tempSet = new HashSet<String>();//�������1�е�Ԫ��
		
		for(int i=0; i<strArray1.length; i++){
			tempSet.add(strArray1[i]);
		}
		
		for(int i=0; i<strArray2.length; i++){
			//������2�е�Ԫ����ӵ�tempSet��
		    //���tempSet���Ѵ�����ͬ��Ԫ�أ���tempSet.add��array2[j]������false
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
}

package com.hbd.retrieval.common.util;

import java.util.List;

import com.hbd.retrieval.common.domain.DocBuilderConfig;


/**
 * ����ThreadLocal��װ�����ļ�
 * @author Administrator
 *
 */
public class ConfigManager {
	private static ThreadLocal<List<DocBuilderConfig>> configHolder = new ThreadLocal <List<DocBuilderConfig>>();
	
	public static List<DocBuilderConfig> getConfigList(){
		List<DocBuilderConfig> configList = configHolder.get();
		//�����ǰ�س���û�а���Ӧ��Config
		if(configList == null){
			configList = XmlConfigReader.getInstance().getConfigList();
			configHolder.set(configList);
		}
		return configList;
	}
}

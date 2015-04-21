package com.hbd.retrieval.common.util;

import java.util.List;

import com.hbd.retrieval.common.domain.DocBuilderConfig;


/**
 * 采用ThreadLocal封装配置文件
 * @author Administrator
 *
 */
public class ConfigManager {
	private static ThreadLocal<List<DocBuilderConfig>> configHolder = new ThreadLocal <List<DocBuilderConfig>>();
	
	public static List<DocBuilderConfig> getConfigList(){
		List<DocBuilderConfig> configList = configHolder.get();
		//如果当前县城中没有绑定相应的Config
		if(configList == null){
			configList = XmlConfigReader.getInstance().getConfigList();
			configHolder.set(configList);
		}
		return configList;
	}
}

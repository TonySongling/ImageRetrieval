package com.hbd.retrieval.common.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.hbd.retrieval.common.domain.DocBuilderConfig;


/**
 * 读取XML文件
 * @author Administrator
 *
 */
public class XmlConfigReader {
	
	/**
	 * 单例模式
	 */
	private static XmlConfigReader instance = null;
	
	public static XmlConfigReader getInstance(){
		if(instance == null)
			instance = new XmlConfigReader();
		return instance;
	}
	private List<DocBuilderConfig> configList = new ArrayList<DocBuilderConfig>();
	
	private XmlConfigReader(){
		SAXReader reader = new SAXReader();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("docBuilder-config.xml");
		try {
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			@SuppressWarnings("unchecked")
			Iterator<Element> iterator = root.elementIterator();
			while(iterator.hasNext()){
				Object node = iterator.next();
				Element el_row = (Element)node;
				
				@SuppressWarnings("unchecked")
				Iterator<Element> it_row = el_row.elementIterator();
				
				DocBuilderConfig config = new DocBuilderConfig();
				while(it_row.hasNext()){
					Element el_ename = (Element) it_row.next();
					String name = el_ename.getText();
					if(!name.contains("index")){
						config.setBuilderName(name);
					}else{
						config.setIndexFolderName(name);
					}
					//System.out.println(el_ename.getText());
				}
				configList.add(config);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public List<DocBuilderConfig> getConfigList(){
		return configList;
	}
}

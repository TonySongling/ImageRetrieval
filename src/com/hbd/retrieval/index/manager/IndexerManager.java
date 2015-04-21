package com.hbd.retrieval.index.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.DocumentBuilderFactory;
import net.semanticmetadata.lire.imageanalysis.LireFeature;
import net.semanticmetadata.lire.imageanalysis.SimpleColorHistogram;
import net.semanticmetadata.lire.imageanalysis.SurfFeature;
import net.semanticmetadata.lire.impl.GenericDocumentBuilder;
import net.semanticmetadata.lire.utils.FileUtils;
import net.semanticmetadata.lire.utils.LuceneUtils;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;

import com.hbd.retrieval.common.util.CommonUtils;
import com.hbd.retrieval.common.util.Constants;

public class IndexerManager {
	//采用单例模式
	private static IndexerManager instance = null;
	
	public static IndexerManager getInstance(){
		if(instance == null)
			instance = new IndexerManager();
		return instance;
	}
	
	public void createIndex(String imgPath, String builderName, String builderFolderName){
		 // Checking if arg[0] is there and if it is a directory.
        boolean passed = false;
        if (imgPath.length() > 0) {
            File file = new File(imgPath);
            System.out.println("Indexing images in " + imgPath);
            if (file.exists() && file.isDirectory()) passed = true;
        }
        if (!passed) {
            System.out.println("No directory given as first argument.");
            System.out.println("Run \"Indexer <directory>\" to index files of a directory.");
            System.exit(1);
        }
        // 获取图片
        ArrayList<String> images = null;
		try {
			images = FileUtils.getAllImages(new File(imgPath), true);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
        // Use multiple DocumentBuilder instances:
		DocumentBuilder builder = null;
		
		int featureIndex = 0;
		if(Constants.CEDD.equals(builderName)){
			featureIndex = Constants.CEDD_index;
		}else if(Constants.FCTH.equals(builderName)){
			featureIndex = Constants.FCTH_index;
		}else if(Constants.Tamura.equals(builderName)){
			featureIndex = Constants.FCTH_index;
		}else if(Constants.SurfFeature.equals(builderName)){
			featureIndex = Constants.SurfFeature_index;
		}else if(Constants.SimpleColorHistogram.equals(builderName)){
			featureIndex = Constants.SimpleColorHistogram_index;
		}
		switch(featureIndex){
			case 0:
				builder = DocumentBuilderFactory.getCEDDDocumentBuilder();
				break;
			case 1:
				builder = DocumentBuilderFactory.getFCTHDocumentBuilder();
				break;
			case 2:
				builder = DocumentBuilderFactory.getTamuraDocumentBuilder();
			case 3:
				builder = new GenericDocumentBuilder((Class<? extends LireFeature>) SurfFeature.class);
			case 4:
				builder = new GenericDocumentBuilder((Class<? extends LireFeature>) SimpleColorHistogram.class);
		}
		
        // 创建IndexWriter
        @SuppressWarnings("deprecation")
		IndexWriterConfig conf = new IndexWriterConfig(LuceneUtils.LUCENE_VERSION,
                new WhitespaceAnalyzer(LuceneUtils.LUCENE_VERSION));
        IndexWriter iw = null;
		try {
			File f = new File(builderFolderName);
			if(f.list() != null){
				CommonUtils.delelteFile(new File(builderFolderName));
				f.mkdir();
			}
			iw = new IndexWriter(FSDirectory.open(new File(builderFolderName)), conf);
			 // Iterating through images building the low level features
	        for (Iterator<String> it = images.iterator(); it.hasNext(); ) {
	            String imageFilePath = it.next();
	            System.out.println("Indexing " + imageFilePath);
	            try {
	                BufferedImage img = ImageIO.read(new FileInputStream(imageFilePath));
	                Document document = builder.createDocument(img, imageFilePath);
	                iw.addDocument(document);
	            } catch (Exception e) {
	                System.err.println("Error reading image or indexing it.");
	                e.printStackTrace();
	            }
	        }
	        iw.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        System.out.println("Finished indexing.");
	}
	
}

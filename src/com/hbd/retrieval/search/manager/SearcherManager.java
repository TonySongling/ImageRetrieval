package com.hbd.retrieval.search.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.DocumentBuilder;
import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.imageanalysis.CEDD;
import net.semanticmetadata.lire.imageanalysis.FCTH;
import net.semanticmetadata.lire.imageanalysis.SimpleColorHistogram;
import net.semanticmetadata.lire.imageanalysis.SurfFeature;
import net.semanticmetadata.lire.imageanalysis.Tamura;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

import com.hbd.retrieval.common.util.Constants;

public class SearcherManager {
	private static SearcherManager instance = null;
	
	public static SearcherManager getInstance(){
		if(instance == null)
			instance = new SearcherManager();
		return instance;
	}
	
	public String[] getSearcherResult(String imgPath, String builderName, String indexFolderName){
		 // Checking if imgPath is there and if it is an image.
        BufferedImage img = null;
        boolean passed = false;
        if (imgPath.length() > 0) {
            File file = new File(imgPath);
            if (file.exists()) {
                try {
                    img = ImageIO.read(file);
                    passed = true;
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
        if (!passed) {
            System.out.println("No image given as first argument.");
            System.out.println("Run \"Searcher <query image>\" to search for <query image>.");
            System.exit(1);
        }

        IndexReader ir = null;
		try {
			ir = DirectoryReader.open(FSDirectory.open(new File(indexFolderName)));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ImageSearcher searcher = null;
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
				searcher = new GenericFastImageSearcher(30, CEDD.class);
				break;
			case 1:
				searcher = new GenericFastImageSearcher(30, FCTH.class);
				break;
			case 2:
				searcher = new GenericFastImageSearcher(30, Tamura.class);
				break;
			case 3:
				searcher = new GenericFastImageSearcher(30, SurfFeature.class);
				break;
			case 4:
				searcher = new GenericFastImageSearcher(30, SimpleColorHistogram.class);
				break;
		}

        // searching with a image file ...
		ImageSearchHits hits = null;
		try {
			hits = searcher.search(img, ir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String[] result = new String[hits.length()];
		// searching with a Lucene document instance ...
		for (int i = 0; i < hits.length(); i++) {
			String fileName = hits.doc(i).getValues(DocumentBuilder.FIELD_NAME_IDENTIFIER)[0];
			result[i] = fileName;
		}
        return result;
	}
	
	/*
	public static void main(String[] args){
		File directory = new File("");//设定为当前文件夹
		String currentPath = directory.getAbsolutePath();
		try{ 
		    System.out.println(directory.getCanonicalPath());//获取标准的路径 
		    System.out.println(directory.getAbsolutePath());//获取绝对路径 
		}catch(Exception e){
			
		} 
		String[] result = SearcherManager.getInstance().getSearcherResult(currentPath + "\\Image 026.jpg", "CEDD", "index-CEDD");
		for(int i=0; i<result.length; i++){
			System.out.println(result[i]);
		}
	}
	*/
}

package com.hbd.retrieval.search.manager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import net.semanticmetadata.lire.ImageSearchHits;
import net.semanticmetadata.lire.ImageSearcher;
import net.semanticmetadata.lire.impl.GenericFastImageSearcher;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.FSDirectory;

public class SearcherManager {
	private static SearcherManager instance = null;
	
	public static SearcherManager getInstance(){
		if(instance == null)
			instance = new SearcherManager();
		return instance;
	}
	
	public ImageSearchHits getSearcherResult(String imgPath, String builderName, String indexFolderName) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
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
		//通过反射机制创建searcher
		Object feature  = Class.forName(builderName).newInstance();
		searcher = new GenericFastImageSearcher(5, feature.getClass());
		
        // searching with a image file ...
		ImageSearchHits hits = null;
		try {
			hits = searcher.search(img, ir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
        return hits;
	}
}

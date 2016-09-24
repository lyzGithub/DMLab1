package coreCompute;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cleanSource.coreClean;

public class vectorCom{
	
	private String kernerPath = "ICML/7. Kernel Methods";
	private int allPaper = 0;
	
	private HashMap<String,HashMap<String,Integer>> papersMap;//one paper, one key words is value list
	private HashMap<String,Integer> paperWordsNumMap;//file name, word count.
	
	private HashMap<String,HashMap<String,Double>> kernerTFIDF;  //filename, vector
	private coreClean cc;
	
	private List<String> allWordsList;//all words contain
	private List<String> kernerFileNameList;//
	
	
	public vectorCom() throws IOException, InterruptedException{
		System.out.println("in the public vectorCom()");
		kernerTFIDF = new HashMap<String,HashMap<String,Double>>();
		paperWordsNumMap = new HashMap<String,Integer>();
		papersMap = new HashMap<String,HashMap<String,Integer>>();
		allWordsList = new ArrayList<String>();
		kernerFileNameList = new ArrayList<String>();
		cc = new coreClean();
		
		allPaper = cc.getPapersNum();
		papersMap = cc.getPapersMap();
		paperWordsNumMap = cc.getPaperWordsForEach();
		allWordsList = cc.getAllWordsMap();
		
		traverseFolder(kernerPath);
		dealEachKerner7();
		writeVerctorToDisk();
		
	}
	public void writeVerctorToDisk() throws IOException{
		//building file info
		System.out.println("in the public vectorCom() writeVerctorToDisk()");
		System.out.println("building file info");
		Iterator iter = kernerTFIDF.entrySet().iterator();
		StringBuilder allVector = new StringBuilder();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String fileName = (String) entry.getKey();
			HashMap<String, Double> valMap = (HashMap<String, Double>) entry.getValue();
			int i = 0;
			StringBuilder strB = new StringBuilder();
			strB.append('[');
			for(Iterator it2 = allWordsList.iterator();it2.hasNext(); i++){//�ֵ���д������
	             String words = (String)it2.next();
	             if(valMap.containsKey(words)){
	            	 double tf_idf = valMap.get(words);
	            	 String temp = Integer.toString(i)+":"+Double.toString(tf_idf)+",";
	            	 strB.append(temp);
	             }
	        }
			strB.deleteCharAt(strB.length()-1);
			strB.append("]\n");
			allVector.append(strB);
		}
		System.out.println("writing result file!");
		//д���ļ�
		File file = new File("result.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
		   file.createNewFile();
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		bw.write(allVector.toString());
		bw.close();
		System.out.println("write result file done!");
		
		
		//д�뵥�ʱ�
		System.out.println("building  wordslist file !");
		StringBuilder wordsListBuilder = new StringBuilder();
		for(Iterator itW = allWordsList.iterator();itW.hasNext();){//�ֵ���д������
			String word = (String)itW.next();
			wordsListBuilder.append(word);
		}
		System.out.println("writing wordslist file!");
		File allWordsFile = new File("wordsOrdering.txt");
		if (!allWordsFile.exists()) {
			allWordsFile.createNewFile();
		}
		BufferedWriter bwAll = new BufferedWriter(new FileWriter(file));
		bwAll.write(wordsListBuilder.toString());
		bwAll.close();
		System.out.println("write wordslist file done!");
	}
	/*
	private void reduceRusult(){//ÿƪ�������ֵ���
		Iterator iter = kernerTFIDF.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String fileName = (String) entry.getKey();
			HashMap<String, Double> valMap = (HashMap<String, Double>) entry.getValue();
			
			// ����ǰ
			List<Map.Entry<String, Double>> infoIds = new ArrayList<Map.Entry<String, Double>>(valMap.entrySet());
			
			// ����
			Collections.sort(infoIds, new Comparator<Map.Entry<String, Double>>() {
				public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
					// return (o2.getValue() - o1.getValue());
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});
			
			kernerTFIDF.put(fileName, valMap);
			
		}
	}
	*/
	private  void traverseFolder(String path) throws IOException, InterruptedException {
		System.out.println("in the public vectorCom() traverseFolder");
			int fileNum = 0, folderNum = 0;
			File file = new File(path);
			if (file.exists()) {
				LinkedList<File> list = new LinkedList<File>();
				File[] files = file.listFiles();
				for (File file2 : files) {
					if (file2.isDirectory()) {
						//System.out.println("�ļ���:" + file2.getAbsolutePath());
						list.add(file2);
						folderNum++;
					} else {
						//System.out.println("�ļ�:" + file2.getName());
						kernerFileNameList.add(file2.getName());
						fileNum++;
					}
				}
				File temp_file;
				while (!list.isEmpty()) {
					temp_file = list.removeFirst();
					files = temp_file.listFiles();
					for (File file2 : files) {
						if (file2.isDirectory()) {
							//System.out.println("�ļ���:" + file2.getAbsolutePath());
							list.add(file2);
							folderNum++;
						} else {
							//System.out.println("�ļ�:" + file2.getPath());
							kernerFileNameList.add(file2.getName());
							fileNum++;
						}
					}
				}
			} else {
				System.out.println("�ļ�������!");
			}
			Collections.sort(kernerFileNameList);
			System.out.println("�ļ��й���:" + folderNum + ",�ļ�����:" + fileNum+ "\n" +kernerFileNameList.toString());
	}
	
	
	
	//����tf-idf
	
	private void dealEachKerner7(){
		System.out.println("in the public vectorCom() dealEachKerner7()");
		int i =0;
		double tf = 0;
		double idf = 0;
		int papersAllCount = allPaper;
		System.out.println("ready to approch bug!");
		for(i = 0; i<kernerFileNameList.size(); i++ ){
			String fileName = kernerFileNameList.get(i);
			//System.out.println(fileName);
			HashMap<String,Integer> fileWordsMap = papersMap.get(fileName);
			
			System.out.println("firest worming! + fileName:" + fileName);
			System.out.println(fileName+ "~~~~ is contain?");
			if(paperWordsNumMap.containsKey(fileName))
				System.out.println(fileName+ "~~~~ is contain!");
			
			int fileWordsNum = paperWordsNumMap.get(fileName);
			
			HashMap<String,Double> tfIDFMap = new HashMap<String,Double>();
			Iterator iter = fileWordsMap.entrySet().iterator();
			while (iter.hasNext()) {//����ÿһƪ�����е��ʵ�TF-IDF
				Map.Entry entry = (Map.Entry) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				tf = (int)val/fileWordsNum;
				
				int exIn = 0;
				Iterator iterAll = papersMap.entrySet().iterator();//����ÿһƪ���鿴���ʴ��ڵ�paper
				while (iter.hasNext()) {
					Map.Entry entryAll = (Map.Entry) iter.next();
					Object valAll = entry.getValue(); 
					if(((HashMap<String,Integer>)valAll).containsKey((String)key) ){
						exIn++;
					}
				}
				
				idf = Math.log(papersAllCount/exIn+1);
				tfIDFMap.put((String)key, tf*idf);
			}
			kernerTFIDF.put(fileName, tfIDFMap);
		}
	}
	
	
}
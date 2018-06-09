/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/8/22 15:58</create-date>
 *
 * <copyright file="TextRank.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014, 上海林原信息科技有限公司. All Right Reserved, http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.hanlp.summary;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.Config;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Dijkstra.DijkstraSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.util.*;

/**
 * TextRank 自动摘要
 *
 * @author hankcs
 */
public class TextRankSentenceMultiThreading
{  

    /**
     * 阻尼系数（ＤａｍｐｉｎｇＦａcｔｏｒ），一般取值为0.85
     */
    final static double d = 0.85;
    /**
     * 最大迭代次数
     */
    final static int max_iter = 200;
    final static double min_diff = 0.001;
    /**
     * 文档句子的个数
     */
    int D;
    /**
     * 拆分为[句子[单词]]形式的文档
     */
    List<List<String>> docs;
    /**
     * 排序后的最终结果 score <-> index
     */
    static TreeMap<Double, Integer> top;

    /**
     * 句子和其他句子的相关程度
     */
    double[][] weight;
    /**
     * 该句子和其他句子相关程度之和
     */
    double[] weight_sum;
    /**
     * 迭代之后收敛的权重// 最後的PageRanking值
     */
    double[] vertex;

    //
    BM25 bm26;
              
    public TextRankSentenceMultiThreading(List<List<String>> docs)//计算BM25相关性矩阵
    {   
    	System.out.println("BiDirections For TextRankSentenceMultiThreading here。");
        
        this.docs = docs;

        
        bm26=new BM25(docs);
        
        /////////////////////////////////
        D = docs.size();
        weight = new double[D][D];
        weight_sum = new double[D];
        vertex = new double[D];
        top = new TreeMap<Double, Integer>(Collections.reverseOrder());
        solve();
    }
      
    private void solve()///迭代投票
    {
        int cnt = 0;
        for (List<String> sentence : docs)//把docs裡的List<String>放入sentence
        {
        
            double[] scores=bm26.simAll(sentence);
            
        	//
            System.out.println("印出經過bm25+1的計算後陣列數值"+Arrays.toString(scores)); //列出相似矩陣的值
            ///////////////////////
            weight[cnt] = scores;
            weight_sum[cnt] = sum(scores) - scores[cnt]; // 减掉自己，自己跟自己肯定最相似
            vertex[cnt] = 1.0;
            ++cnt;
        }
        for (int _ = 0; _ < max_iter; ++_)
        {
            double[] m = new double[D];
            for (int i = 0; i < D; ++i)
            {
                m[i] = 1 - d;
                for (int j = 0; j < D; ++j)
                {
                    if (j == i || weight_sum[j] == 0) continue;
                    m[i] += (d * weight[j][i] / weight_sum[j] * vertex[j]);//(由于是有权图，PageRank公式)
                }
            }
            vertex = m;

        }
        // 我们来排个序吧
        for (int i = 0; i < D; ++i)
        {
            top.put(vertex[i], i);
        }
        
        ///////////
        System.out.println("keySet: "+top.keySet());
        System.out.println("keyValue: "+top.values());

    }
    //////////
    public static int getSize(){
    	
    	return top.size();

    }
    public static int[] getTop()
    {
        Collection<Integer> values = top.values();//即找vertex[i]的 i 索引
        
        int[] indexArray = new int[top.size()];
        Iterator<Integer> it = values.iterator();
        for (int i = 0;i<top.size() ; ++i)
        {
            indexArray[i] = it.next();
        }
        return indexArray;
    }
    /**
     * 获取前几个关键句子
     *
     * @param size 要几个
     * @return 关键句子的下标
     */
    public int[] getTopSentence(int size)
    {
        Collection<Integer> values = top.values();//即找vertex[i]的 i 索引
        size = Math.min(size, values.size());
        int[] indexArray = new int[size];
        Iterator<Integer> it = values.iterator();
        for (int i = 0; i < size; ++i)
        {
            indexArray[i] = it.next();
        }
        return indexArray;
    }
    
    /////////////////////////////////////
    
    /**
     * 简单的求和
     *
     * @param array
     * @return
     */
    private static double sum(double[] array)
    {
        double total = 0;
        for (double v : array)
        {
            total += v;
        }
        return total;
    }
    
    
    //
    /**
     * 将文章分割为句子
     *
     * @param document
     * @return
     */
    
    static List<String> spiltSentence(String document)//斷句 
    {
        List<String> sentences = new ArrayList<String>();
        for (String line : document.split("[\r\n]"))
        {
            line = line.trim();
            if (line.length() == 0) continue;
            for (String sent : line.split("[，,。:：“”？?！!；;]"))
            {
                sent = sent.trim();
                if (sent.length() == 0) continue;
                sentences.add(sent);
            }
        }

        return sentences;
    }
    ////////////////////////////////////////////////
    //分詞(Segment)在這裡做完成//////////////////////
    /**
     * 将句子列表转化为文档
     *
     * @param sentenceList
     * @return
     */
   
    
   private static List<List<String>> convertSentenceListToDocument(String sentence)//分詞和過濾停用詞
   {
        
        //  使用多線程 平行計算      
       Segment shortestSegment=new DijkstraSegment().enableMultithreading(false);
       
       double sum=0; 
       
	   for(int cnt=0;cnt<5;cnt++) 
	   {
	    	
	    
        sum=0;
        //
        
        int pressure = 1000;
        String text="";
        StringBuilder sbBigText = new StringBuilder(sentence.length() * pressure);
        for (int i = 0; i < pressure; i++)
        {
            sbBigText.append(sentence);
        }
        text = sbBigText.toString();
        
        
        long start = System.currentTimeMillis();
        shortestSegment.seg(text);
        double costTime = (System.currentTimeMillis() - start) ;         
        System.gc();
         
        
        
    	sum+=costTime;    	
    	
        System.out.printf("ShortestSegment分词總時間:%.5f\n毫秒",sum); 
        System.out.printf("線程數量=%d\n",Config.threadNumber);
        
       
	    
	   }
	    
	    
        System.out.printf("\n長度:%s\n",sentence.length());
        

        
       	List<List<String>> docs = new ArrayList<List<String>>(Config.sentenceListNum);
       	      	
       	List<Term> termList=shortestSegment.seg(sentence);   	
        List<String> wordList = new LinkedList<String>();
           
           ////////////////////////////////////////////
           for (Term term : termList)
           {   

               if (CoreStopWordDictionary.shouldInclude(term))//過濾不要的詞，加入需要分析的詞進去wordList
               {   

                	   wordList.add(term.word); 
               }
               
               if(term.word.contains("。") ||term.word.contains("，"))
               {            	   
	                   docs.add(wordList);
	            	   wordList=new LinkedList<String>();
	            	   continue;         
               }
          
           }

       
       
       
       return docs;
   }
    /**
     * 一句话调用接口
     *
     * @param document 目标文档
     * @param size     需要的关键句的个数 //thread_size
     * @return 关键句列表
     */
    
    //自動摘要主要使用的部分
    public static List<String> getTopSentenceList(String document, int thread_size)
    {   
    	Config.threadNumber=thread_size; //把thread_num丟給Config.threadNumber 靜態變數，讓大家都是一樣的線程數。
    	
	   	//////////////////////////因為裡面CoreNatureDictionary是簡體字典 所以先做轉換
		String text_simplified = HanLP.convertToSimplifiedChinese(document);		
		List<List<String>> docs = convertSentenceListToDocument(text_simplified);//分詞和過濾停用詞
        
		//
		System.out.println("印出docs="+docs+" ");
        TextRankSentenceMultiThreading textRank = new TextRankSentenceMultiThreading(docs);//计算BM25相关性矩阵
        
        int ratio_count=Config.sentenceListNum;
        int[] topSentence = textRank.getTopSentence(ratio_count);
        List<String> sentenceList_traditional=spiltSentence(document); //再做一次，抓原先繁體句子的index
 
        
        List<String> resultList = new LinkedList<String>();
        for (int i : topSentence)
        {
        	resultList.add(sentenceList_traditional.get(i)+"   ");//get(i)取得句子的index
        }
        return resultList;
                
    }
    
    

}

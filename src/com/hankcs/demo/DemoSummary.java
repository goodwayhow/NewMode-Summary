/*
 * <summary></summary>
 * <author>He Han</author>
 * <email>hankcs.cn@gmail.com</email>
 * <create-date>2014/12/7 19:25</create-date>
 *
 * <copyright file="DemoChineseNameRecoginiton.java" company="上海林原信息科技有限公司">
 * Copyright (c) 2003-2014+ 上海林原信息科技有限公司. All Right Reserved+ http://www.linrunsoft.com/
 * This source is subject to the LinrunSpace License. Please contact 上海林原信息科技有限公司 to get more information.
 * </copyright>
 */
package com.hankcs.demo;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.summary.TextRankSentence;

import java.util.List;

/**
 * 自动摘要
 * @author hankcs
 */
/*
public class DemoSummary
{
    public static void main(String[] args)
    {
        String document = "水利部水资源司司长陈明忠9月29日在国务院新闻办举行的新闻发布会上透露，" +
                "根据刚刚完成了水资源管理制度的考核，有部分省接近了红线的指标，" +
                "有部分省超过红线的指标。对一些超过红线的地方，陈明忠表示，对一些取用水项目进行区域的限批，" +
                "严格地进行水资源论证和取水许可的批准。";
        List<String> sentenceList = HanLP.extractSummary(document, 3);
        System.out.println(sentenceList);
    }
}
*/

//import hanlp.HanLP;
import com.hankcs.hanlp.summary.TextRankSentence;
import com.hankcs.hanlp.summary.TextRankSentenceMultiThreading;

import javax.swing.*;
import java.util.List;
//import java.util.Scanner;
import java.awt.*;
//import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.awt.Container;
import java.awt.FlowLayout;
/**
* 自动摘要
*/

public class DemoSummary extends JFrame implements ActionListener
{   
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static DemoSummary mainFrame=new DemoSummary();
    static JButton btn_viterbi=new JButton();
    static JButton btn_dijkstra=new JButton();
    static JButton btn_analyze=new JButton();
    static JButton btn_close=new JButton();
  
  //static JLabel label3=new JLabel();
  
    static TextArea field=new TextArea(" ",20,200,TextArea.SCROLLBARS_BOTH);
	static TextArea txt=new TextArea("",20,200,TextArea.SCROLLBARS_BOTH);
	
	//static TextField tfield=new TextField();
		  
	
  public static void main(String[] args)
  {   
  	  
  	//全選文字框，方便使用者更改
  	  field.selectAll();

        
        //label3.setText("輸入Thread數");
		  //設定按鈕文字
		  btn_dijkstra.setText("Load Dijkstra-MultiThreading");
		  btn_viterbi.setText("StandardTokenizer");
		  btn_analyze.setText("Compare Algorithm");
		  btn_close.setText("Exit");
		  //設定文字標籤提示訊息

        mainFrame.setLayout(new FlowLayout(FlowLayout.LEFT));
		  mainFrame.setSize(200,200);
		  		  
		  mainFrame.add(field);
		  
		  mainFrame.add(btn_dijkstra);
		  mainFrame.add(btn_viterbi);
		  mainFrame.add(btn_analyze);
		  mainFrame.add(btn_close);
		  mainFrame.add(txt);
        mainFrame.setVisible(true);
		   
		  //建立按鈕的監聽事件，呼叫自己這個的類別，去實作ActionListener
		  //和一個actionPerformed方法
		  btn_dijkstra.addActionListener(mainFrame);
		  btn_viterbi.addActionListener(mainFrame);
		  btn_analyze.addActionListener(mainFrame);
		  btn_close.addActionListener(mainFrame);
    
     

  }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		    
		
		    JButton btn2=(JButton) e.getSource();//擷取button按下的選項狀態
		    
		    String text=field.getText(); //輸入文件的field

	        int thread_num=1; 
	     
		    if(btn2==btn_dijkstra)
		    {//顯示
	       
            List<String> sentenceList= TextRankSentenceMultiThreading.getTopSentenceList(text,thread_num);
	        
            System.out.println(sentenceList);
	        System.out.println("\n\n");
		        //顯示分析完的句子
	        
		        txt.setText(sentenceList.toString()); 

          }
          else if(btn2==btn_viterbi)
          {
  	        
            List<String> sentenceList= TextRankSentence.getTopSentenceList(text, thread_num);
  	        
            System.out.println(sentenceList);
  	        System.out.println("\n\n");
	    	        //顯示分析完的句子
	    	        txt.setText(sentenceList.toString());
  	        
          }
		    else if(btn2==btn_analyze)
		    {
		    	
		    	//List<String> sentenceList_Viter=TextRankSentence.getTopSentenceList(text, thread_num); //原先公式
		    	int a[]=TextRankSentence.getTop();
		    	
		    	double cnt_correct=0;
		    	double cnt_wrong=0;
		    	double cnt_missed=0;
		    	int index=0;
		    	int compareindexlength=0;
		    	
		    	
		    	//List<String> sentenceList_Dijk= TextRankSentenceMultiThreading.getTopSentenceList(text,thread_num);// 我的算法
		    	int b[]=TextRankSentenceMultiThreading.getTop();
		    	
		    	
		    	//比較兩個算法indexes長度，選擇較小的
		    	if(TextRankSentence.getSize()<=TextRankSentenceMultiThreading.getSize())
		    	{
		    		compareindexlength=TextRankSentence.getSize();
		    	}
		    	else if(TextRankSentence.getSize() > TextRankSentenceMultiThreading.getSize())
		    	{
		    		compareindexlength=TextRankSentenceMultiThreading.getSize();
		    	}
		    	int c[]=new int[compareindexlength];
		    	System.out.println("CompareIndexLength="+compareindexlength);
		    	System.out.println("共同出現的索引");
		    	//
		    	
              for(int i=0;i<compareindexlength;i++)
              {
              	for(int j=0;j<compareindexlength;j++)
              	{
              		if(a[i]==b[j])
              		{
              		System.out.print(a[i]+" ");
              		c[index++]=a[i];
              		cnt_correct++;      //共同有出現的索引 by my own and by the system
              		break; //if it's executing the break , jumping the current  loop , and going to the outer loop to execute.
              		}

              	}
              }
              
	                for(int i=0;i<compareindexlength;i++){
	                	for(int j=0;j<cnt_correct;j++){
	                		
	                		if(b[i]==c[j])
	                		{     
	                           break;
	                		}
		                		if(j==cnt_correct-1)
		                		{
			                	cnt_missed++;
			                	System.out.print("\nMissed的索引="+b[i]); // by the human but not by the system
			                	}		            
	                	}
	                	
	                }
	                
		                for(int i=0;i<compareindexlength;i++){
		                	for(int j=0;j<cnt_correct;j++){
		                		if(a[i]==c[j])
		                		{     
		                           break;
		                		}
			                		if(j==cnt_correct-1)
			                		{
				                	cnt_wrong++;
				                	System.out.print("\nWrong的索引="+a[i]); //by the system but not by the human;  
				                	}
		                	}
		                }
		      System.out.println("\n---------------------------");        
              
		      System.out.println("\nCorrect次數="+cnt_correct);
              System.out.println("\nWrong  次數="+cnt_wrong);
              System.out.println("\nMissed 次數="+cnt_missed);
              
              double precision=cnt_correct/(cnt_correct+cnt_wrong);
              double recall=cnt_correct/(cnt_correct+cnt_missed);
              double f_measure=2*precision*recall/(precision+recall);
              
              System.out.println("\nPrecision(P)="+precision);
              System.out.println("\nRecall(R)="+recall);
              System.out.println("\nF_Measure(F-M)="+f_measure);
              System.out.println("\n\n");
		    }
          else
          {
          	System.exit(0);
          }
         
	        
		
	}
}

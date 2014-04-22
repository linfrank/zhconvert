package com.soypig.zhconvert;
/*
 * Frank Lin
 * 
 */

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WikipediaListConverter{
	
	public static final Pattern charListPat=Pattern.compile("(.)\\=>(.);");
	public static final Pattern wordListPat=Pattern.compile("\"(.+?)\" ?\\=> ?\"(.+?)\",");
	
	public static String readFile(String fileName)throws Exception{
		
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),"UTF8"));
		StringBuffer buffer=new StringBuffer();
		for(String nextLine;(nextLine=reader.readLine())!=null;buffer.append(nextLine+"\n"));
		reader.close();
		return buffer.toString();
		
	}
	
	public static void writeFile(String fileName,String data)throws Exception{
		
		BufferedReader reader=new BufferedReader(new StringReader(data));
		PrintWriter writer=new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileName),"UTF8"),true);
		for(String nextLine;(nextLine=reader.readLine())!=null;writer.println(nextLine));
		reader.close();
		writer.close();
		
	}
	
	public static void convertList(String inputFile,String outputFile,Pattern pat)throws Exception{
		
		String input=readFile(inputFile);
		StringBuffer buffer=new StringBuffer();
		
		Matcher matcher=pat.matcher(input);
		while(matcher.find()){
			buffer.append(matcher.group(1)+"\t"+matcher.group(2)+"\n");
		}
		
		writeFile(outputFile,buffer.toString());
		
	}
	
	public static void main(String[] args)throws Exception{
		
		convertList("WikipediaCharST.txt","ConvCharST.txt",charListPat);
		convertList("WikipediaCharTS.txt","ConvCharTS.txt",charListPat);
		convertList("WikipediaWordST.txt","ConvWordST.txt",wordListPat);
		convertList("WikipediaWordTS.txt","ConvWordTS.txt",wordListPat);
		
		
	}

}

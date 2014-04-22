package com.soypig.zhconvert;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class ChineseConverter{

	private static class TrieNode{
		public String target=null;
		public Map<Character,TrieNode> children=new HashMap<Character,TrieNode>();
	}

	private static void addEntry(TrieNode node,String source,String target){
		// check for null or zero-length entries
		if(source==null||target==null||source.length()<1||target.length()<1){
			return;
		}
		// if we're good, see if child node exists for the char, if not create it
		char c=source.charAt(0);
		TrieNode child=node.children.get(c);
		if(child==null){
			child=new TrieNode();
			node.children.put(c,child);
		}
		// if we're at the last character, insert the target
		if(source.length()==1){
			child.target=target;
		}
		// else recurse
		else{
			addEntry(child,source.substring(1),target);
		}
	}

	private static void populate(TrieNode dictionary,String convFile){
		try{
			BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(convFile),"UTF8"));
			for(String nextLine;(nextLine=reader.readLine())!=null;){
				String[] tokens=nextLine.split("\t");
				addEntry(dictionary,tokens[0],tokens[1]);
			}
			reader.close();
		}
		catch(Exception e){
			System.err.println(e);
		}
	}

	private static class Match{
		public int length=0;
		public String target=null;
		@Override
		public String toString(){return target+"/"+length;}
	}

	private static void match(TrieNode node,String source,Match match){
		// check for null or zero-length entries
		if(source==null||source.length()<1){
			return;
		}
		char c=source.charAt(0);
		TrieNode child=node.children.get(c);
		// if any of the child node matches
		if(child!=null){
			// increment match stat
			match.length++;
			match.target=child.target;
			// recurse
			match(child,source.substring(1),match);
		}
		// if no match, simply return
	}

	private static Match match(TrieNode dictionary,String source){
		Match match=new Match();
		match(dictionary,source,match);
		return match;
	}

	private TrieNode dictST=new TrieNode();
	private TrieNode dictTS=new TrieNode();

	public ChineseConverter(
			String convCharSTFile,
			String convCharTSFile,
			String convWordSTFile,
			String convWordTSFile){
		
		populate(dictST,convCharSTFile);
		populate(dictST,convWordSTFile);
		populate(dictTS,convCharTSFile);
		populate(dictTS,convWordTSFile);

	}
	
	private String convert(TrieNode dict,String source){
		StringBuilder b=new StringBuilder();
		for(int i=0;i<source.length();){
			Match m=match(dict,source.substring(i));
			if(m.target!=null){
				b.append(m.target);
				i+=m.length;
			}
			else{
				b.append(source.charAt(i));
				i++;
			}
		}
		return b.toString();
	}
	
	public String convertST(String s){
		return convert(dictST,s);
	}
	
	public String convertTS(String s){
		return convert(dictTS,s);
	}

	public static void testST(ChineseConverter cc,String[] source){
		System.out.println("Simplified to Traditional:\n");
		for(String s:source){
			System.out.println(" "+s+" => \n "+cc.convertST(s)+"\n");
		}
	}
	
	public static void testTS(ChineseConverter cc,String[] source){
		System.out.println("Traditional to Simplified:\n");
		for(String s:source){
			System.out.println(" "+s+" => \n "+cc.convertTS(s)+"\n");
		}
	}

	public static void main(String[] args){

		ChineseConverter cc=new ChineseConverter(
				"resources/ConvCharST.txt",
				"resources/ConvCharTS.txt",
				"resources/ConvWordST.txt",
				"resources/ConvWordTS.txt"
				);

		String[] st={
				"皇后在皇帝后面",
				"你有没有笔记本电脑？"
		};

		String[] ts={
				"我優先順序比妳高",
				"我女友是烏茲別克人"
		};
		
		testST(cc,st);
		testTS(cc,ts);

	}


}

package Crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashSet;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	public Crawler() {
		this.frontier = new ArrayDeque<LinkNode>();
		this.visited = new HashSet<String>();
		
		// loadExistingPages();
	}
	
	public Crawler(int depth) {
		this.frontier = new ArrayDeque<LinkNode>();
		this.visited = new HashSet<String>();
		this.MAX_DEPTH = depth;
	}
	
	public Crawler(int depth, int numPages) {
		this.frontier = new ArrayDeque<LinkNode>();
		this.visited = new HashSet<String>();
		this.MAX_DEPTH = depth;
		this.MAX_NUM_PAGES = numPages;
	}
	
	// load names of pages that are downloaded to check redundant pages
	// not precise due to the presence of skipping of "www." in URL
	private void loadExistingPages() {
		File pages = new File(dest);
		File[] list = pages.listFiles();
		
		for(int i = 0; i < list.length; i++) {
			visited.add(list[i].getName().replace(".html", ""));
		}
	}
	
	// create html file named as paramterized filename
	// normalization took place: remove http/https protocol and redundant www.
	private String createTitle(String filename) {
		StringBuilder title = new StringBuilder(dest);
		StringBuilder link = new StringBuilder();
		if(filename.contains("http://")) {
			filename = filename.replace("http://", ""); 
		}
		else if(filename.contains("https://")) {
			filename = filename.replace("https://", ""); 
		}
		else {
			return "";
		}
		if(filename.contains("www.")) {
			filename = filename.replace("www.", "");
		}
		
		link.append(filename);
		if(filename.endsWith("/")) {
			link.deleteCharAt(filename.length() - 1);
		}
		link.append(".html");
		title.append(link.toString().replace("/", "-"));
		
		return title.toString();
	}
	
	public void crawl() {
		// add seed link to frontier, currently just manual
		int numPages = 0;
		String seedFile = "src/seed.txt";
		
		LinkNode seed = new LinkNode("http://alaska.edu");
		frontier.add(seed);
		numPages++;
		// run as long as frontier has some links to crawl
		// for(int i = 0; i < 15; i++){
		while(!frontier.isEmpty()) {
			if(numPages > MAX_NUM_PAGES) {
				// crawler reached page number limit
				return;
			}
			LinkNode curr = frontier.poll();
			numPages++;
			try {
				// if the current page is already visited, skip it
				if(visited.contains(curr.getLink())) {
					continue;
				}
				visited.add(curr.getLink());
				// fetch pages from URL
				Document doc = Jsoup.connect(curr.getLink()).get();
				Connection.Response html = Jsoup.connect(curr.getLink()).execute();
				
				Elements links = doc.select("a[href]");
				
				String title = createTitle(curr.getLink());
				// check title
				System.out.println("Title: " + title);
				
				if(title.equals("")) {
					// current link is not appropriate URL missing http:// or https://
					continue;
				}
				String htmlText = html.body();
				BufferedWriter writer = new BufferedWriter(new FileWriter(title));
				writer.write(htmlText);
				writer.close();
				// to see what pages are visited
				// System.out.println(curr.getLink());
				// System.out.println(html.body());
				for(Element e: links) {
					// if current page reaches max depth
					//	or link is not .edu format
					if(curr.getDepth() >= MAX_DEPTH || !e.absUrl("href").contains(".edu")) {
						continue;
					}
					LinkNode next = new LinkNode(e.absUrl("href"), curr.getDepth() + 1);
					frontier.add(next);
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(curr.getLink() + " cannot be found.");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(curr.getLink() + " is not a proper URL.");
				// e.printStackTrace();
			}
		}
	}
	
	// any data structure implementing Queue should work
	private ArrayDeque<LinkNode> frontier;
	private int MAX_DEPTH = 5;
	private int MAX_NUM_PAGES = 100;
	private final String dest = "src/pages/";
	private HashSet<String> visited;
}

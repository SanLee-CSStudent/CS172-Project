package Crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	public Crawler() {
		this.frontier = new ArrayDeque<LinkNode>(MAX_NUM_PAGES);
		this.visited = new HashSet<String>();
		
		// loadExistingPages();
	}
	
	public Crawler(int depth) {
		this.frontier = new ArrayDeque<LinkNode>(MAX_NUM_PAGES);
		this.visited = new HashSet<String>();
		this.MAX_DEPTH = depth;
	}
	
	public Crawler(int depth, int numPages) {
		this.frontier = new ArrayDeque<LinkNode>(MAX_NUM_PAGES);
		this.visited = new HashSet<String>();
		this.MAX_DEPTH = depth;
		this.MAX_NUM_PAGES = numPages;
	}
	
	// create html file named as paramterized filename
	// normalization took place: remove http/https protocol and redundant www.
	private String createTitle(String filename) {
		StringBuilder address = new StringBuilder(dest);
		// redundant http:// makes files' titles look much more dense
		// remove it for simplicity
		if(filename.contains("http://")) {
			filename = filename.replace("http://", ""); 
		}
		else {
			return "";
		}
		// if the URL contains www., remove it for simplicity
		// URLs that lack www. do not cause prevent users from accessing them
		if(filename.contains("www.")) {
			filename = filename.replace("www.", "");
		}
		
		// to distinguish local directory and web directory, replace / with -
		address.append(filename.replace("/", "-"));
		address.append(".html");
		
		return address.toString();
	}
	
	// reads in seed.txt and store URL to frontier
	public void loadSeeds() {
		String seedFile = "src/seed.txt";
		
		File seed = new File(seedFile);
		Scanner s;
		try {
			s = new Scanner(seed);
			
			while(s.hasNextLine()) {
				// upon creating LinkNode, anchors or references are removed
				LinkNode node = new LinkNode(s.nextLine());
				// if normalization succeeds, add node to the frontier
				// otherwise(i.e. protocol is not http), ignores it and continue to the next seed
				if(node.checkURL()) {
					frontier.add(node);
				}
			}
			s.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File not found");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("Malformed URL is read");
		}
	}
	
	public void crawl() {
		// add seed link to frontier, currently just manually assigned
		loadSeeds();
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
				// before doing any operation make sure to normalize the link
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
				
				if(title.equals("")) {
					// current link is not appropriate URL missing http:// or https://
					continue;
				}
				String htmlText = html.body();
				BufferedWriter writer = new BufferedWriter(new FileWriter(title));
				writer.write(htmlText);
				writer.close();
				// to see what pages are visited
				System.out.println("Title: " + title);
				// System.out.println(curr.getLink());
				// System.out.println(html.body());
				
				for(Element e: links) {
					// if current page reaches max depth, skip it
					if(curr.getDepth() >= MAX_DEPTH) {
						continue;
					}
					
					LinkNode next = new LinkNode(e.absUrl("href"), curr.getDepth() + 1);
					if(next.checkURL()) {
						frontier.add(next);
					}
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println(curr.getLink() + " cannot be found.");
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				// some links are not accessible due to not supported http protocol
				System.out.println("Malformed URL is read");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println("Writing to file failed");
			}
		}
	}
	
	// any data structure implementing Queue should work
	private ArrayDeque<LinkNode> frontier;
	private int numPages = 0;
	private int MAX_DEPTH = 5;
	private int MAX_NUM_PAGES = 100;
	private final String dest = "src/pages/";
	private HashSet<String> visited;
}

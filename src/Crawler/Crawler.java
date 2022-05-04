package Crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.Arrays;
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
	private String createUniqueTitle(LinkNode curr) {
		String filename = curr.getLink();
		StringBuilder address = new StringBuilder(dest);
		// redundant http:// makes files' titles look much more dense
		// remove it for simplicity
		filename = filename.replace(curr.getProtocol() + "://", "");
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
	
	private void readRobots(LinkNode curr) throws IOException {
		String robots = curr.getLink() + "robots.txt";
		Connection.Response accessList = Jsoup.connect(robots).execute();
		
		String accessListText = accessList.body();
		String[] lines = accessListText.split("\n");
		
		HashSet<String> disallowed = new HashSet<String>();
		for(String s: lines) {
			if(s.equals("")) {
				continue;
			}
			if(s.toLowerCase().charAt(0) == "D".toLowerCase().charAt(0)) {
				String[] disallowedPath = s.split(" ");
				if(disallowedPath.length >= 2) {
					disallowed.add(disallowedPath[1]);
				}
			}
		}
		
		curr.setDisallow(disallowed);
	}
	
	// reads in seed.txt and store URL to frontier
	private void loadSeeds() {
		try {
			String seedFile = "src/seed.txt";
			
			File seed = new File(seedFile);
			Scanner s;
	
			s = new Scanner(seed);
			
			while(s.hasNextLine()) {
				// upon creating LinkNode, anchors or references are removed
				LinkNode node = new LinkNode(s.nextLine());
				// if normalization succeeds, add node to the frontier
				// otherwise(i.e. protocol is not http), ignores it and continue to the next seed
				if(node.checkHost()) {
					frontier.add(node);
				}
			}
			s.close();
		} catch(FileNotFoundException e) {
			System.out.println("File not found in pages repository: " + e.getMessage());
		} catch (MalformedURLException e) {
			System.out.println("Malformed URL is read");
		}
	}
	
	public void crawl() {
		// add seed link to frontier, currently just manually assigned
		loadSeeds();
		
		// run as long as frontier has some links to crawl
		while(!frontier.isEmpty()) {
			try {
				if(numPages > MAX_NUM_PAGES) {
					// crawler reached page number limit
					return;
				}
				LinkNode curr = frontier.poll();
				numPages++;
				
				// check if the current page is already visited
				// check if current page is in file formats to prevent download them
				// i.e. .pdf .jpg .png ...
				if(visited.contains(curr.getLink()) || curr.isInvalidFiles()) {
					continue;
				}
				
				// root domain will contain robots.txt
				if(curr.getDepth() == 1) {
					readRobots(curr);
				}
				String title = createUniqueTitle(curr);
				
				// fetch pages from URL
				Document doc = Jsoup.connect(curr.getLink()).get();
				Connection.Response html = Jsoup.connect(curr.getLink()).execute();
				
				String htmlText = html.body();
				BufferedWriter writer = new BufferedWriter(new FileWriter(title));
				writer.write(htmlText);
				writer.close();
				
				// only stored pages go to visited
				visited.add(title);
				
				// to see what pages are visited
				System.out.println("Title: " + title);
				
				// if current protocol is not http or is a file that cannot be parsed, stop parsing the URL 
				// otherwise, parse the URL and extract next URLs
				if(!curr.isHTTP()) {
					continue;
				}
				
				Elements links = doc.select("a[href]");
				for(Element e: links) {
					// if current page reaches max depth, skip it
					if(curr.getDepth() >= MAX_DEPTH) {
						continue;
					}
					String nextLink = e.absUrl("href");
	
					if(!nextLink.contains("http")) {
						continue;
					}
					LinkNode next = new LinkNode(nextLink, curr.getDepth() + 1);
					next.setDisallow(curr.getRobots());
					if(next.checkURL()) {
						frontier.add(next);
					}
				}
			} catch (FileNotFoundException e) {
				System.out.println("File Not found.");
			} catch (MalformedURLException e) {
				// some links are not accessible due to not supported protocols(i.e. tel? or virtual assistants)
				System.out.println("Malformed URL is read");
			} catch (UnknownHostException e) {
				// host name is not found
				System.out.println("Unknown Host: " + e.getMessage());
			} catch (IOException e) {
				System.out.println("Writing to file failed");
			}
		}
		System.out.println("Frontier is empty");
	}
	
	// any data structure implementing Queue should work
	private ArrayDeque<LinkNode> frontier;
	private int numPages = 0;
	private int MAX_DEPTH = 5;
	private int MAX_NUM_PAGES = 100;
	private final String dest = "src/pages/";
	private HashSet<String> visited;
}

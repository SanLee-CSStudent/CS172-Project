package Crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	public Crawler() {
		this.frontier = new ArrayDeque<LinkNode>();
	}
	
	public Crawler(int depth) {
		this.MAX_DEPTH = depth;
	}
	// create html file named as paramterized filename
	private String createTitle(String filename) {
		StringBuilder title = new StringBuilder(dest);
		if(filename.contains("http://")) {
			title.append(filename.replace("http://", "")); 
		}
		else if(filename.contains("https://")) {
			title.append(filename.replace("https://", "")); 
		}
		else {
			return "";
		}
		title.append(".html");
		
		return title.toString();
	}
	
	public void crawl() {
		// add seed link to frontier, currently just manual
		LinkNode seed = new LinkNode("http://alaska.edu");
		frontier.add(seed);
		// run as long as frontier has some links to crawl
		for(int i = 0; i < 1; i++){
		// while(!frontier.isEmpty()) {
			LinkNode curr = frontier.poll();
			try {
				// fetch pages from URL
				Document doc = Jsoup.connect(curr.getLink()).get();
				Connection.Response html = Jsoup.connect(curr.getLink()).execute();
				
				Elements links = doc.select("a[href]");
				
				String title = createTitle(curr.getLink());
				if(title.equals("")) {
					// current link is not appropriate URL
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
					if(curr.getDepth() >= MAX_DEPTH || !curr.getLink().contains(".edu")) {
						continue;
					}
					LinkNode next = new LinkNode(e.attr("abs:href"), curr.getDepth() + 1);
					frontier.add(next);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println(curr.getLink() + "is not a proper URL.");
			}
		}
	}
	
	// any data structure implementing Queue should work
	private ArrayDeque<LinkNode> frontier;
	private int MAX_DEPTH = 5;
	private final String dest = "src/pages/";
}

package Crawler;

import java.io.IOException;
import java.util.ArrayDeque;

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
	
	public void crawl() {
		// add seed link to frontier, currently just manual
		LinkNode seed = new LinkNode("http://alaska.edu");
		frontier.add(seed);
		
		// run as long as frontier has some links to crawl
		// for(int i = 0; i < 2; i++){
		while(!frontier.isEmpty()) {
			LinkNode curr = frontier.poll();
			try {
				Document doc = Jsoup.connect(curr.getLink()).get();
				// somehow download html files here?
				Elements links = doc.select("a[href]");
				// to see what websites are visited
				// System.out.println(curr.getLink());
				for(Element e: links) {
					if(curr.getDepth() >= MAX_DEPTH) {
						continue;
					}
					LinkNode next = new LinkNode(e.attr("abs:href"), curr.getDepth() + 1);
					frontier.add(next);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	// any data structure implementing Queue should work
	private ArrayDeque<LinkNode> frontier;
	private int MAX_DEPTH = 5;
}

package Crawler;

public class LinkNode {
	public LinkNode(String link) {
		this.link = link;
		depth = 1;
	}
	
	public LinkNode(String link, int depth) {
		this.link = link;
		this.depth = depth;
	}
	
	public String getLink() {
		return link;
	}
	
	public int getDepth() {
		return depth;
	}
	
	private String link;
	private int depth;
}

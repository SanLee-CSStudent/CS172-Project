package Crawler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;

public class LinkNode {
	
	// root node constructor
	public LinkNode(String link) throws MalformedURLException {
		link = removeRef(link);
		
		// for the starting node, root is itself
		this.link = new URL(link);
		// get local address of URL
		this.local = this.link.getFile();
		// if root does not contain slash(/) at the end, add it
		// normalizing to prevent duplicate pages
		if(this.local.equals("")) {
			this.link = null;
			this.link = new URL(link + "/");
			this.local = "/";
		}
		depth = 1;
	}
	
	// child node constructors
	public LinkNode(String link, int depth) throws MalformedURLException {
		link = removeRef(link);
		
		this.link = new URL(link);
		this.local = this.link.getFile();
		this.depth = depth;
	}
	
	// from the URL, this function decides whether URL is functional or not
	public boolean checkURL() {
		// if the local path is included in the robots.txt
		if(robots == null) {
			return true;
		}
		if(robots.contains(this.getLocal())) {
			return false;
		}
		// if there exists an anchor, remove it
		return true;
	}
	
	// check if the link is in http protocol or not
	public boolean isHTTP() {
		if(link.getProtocol().equals("http")) {
			return true;
		}
		return false;
	}
	
	// check if the URL points to specific files
	// since files other than html will not be parsed, they will be ignored by crawlers
	public boolean isInvalidFiles() {
		if(link.getFile().contains(".pdf")) {
			return true;
		}
		else if(link.getFile().contains(".pdf")) {
			return true;
		}
		else if(link.getFile().contains(".gif")) {
			return true;
		}
		if(link.getFile().contains(".jpg") || link.getFile().contains(".jpeg")) {
			return true;
		}
		return false;
	}
	
	// check if the URL links to .edu hosts
	// if it does not contain .edu, ignore the URL
	public boolean checkHost() {
		if(link.getHost().contains(".edu")) {
			return true;
		}
		return false;
	}
	
	// remove bookmarks/references noted by "#"
	private String removeRef(String link){
		if(link.contains("#")) {
			return link.substring(0, link.indexOf("#") - 1);
		}
		
		return link;
	}
	
	// return URL protocol: http, https, ftp...
	public String getProtocol() {
		return link.getProtocol();
	}
	
	// return local address: /a/b/c...
	public String getLocal() {
		return local;
	}
	
	// return the complete link
	public String getLink() {
		return link.toString();
	}
	
	// return the recursion depth of the current node
	public int getDepth() {
		return depth;
	}
	
	// set robots disallow list to the current node
	public void setDisallow(HashSet<String> robots) {
		this.robots = robots;
	}
	
	// return robots disallow list
	public HashSet<String> getRobots() {
		return robots;
	}
	
	private URL link;
	private int depth;
	private String local;
	private HashSet<String> robots;
}

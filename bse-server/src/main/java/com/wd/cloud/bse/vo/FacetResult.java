package com.wd.cloud.bse.vo;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计结果
 * @author shenfu
 *
 */
public class FacetResult{
	
	/**
	 * 具体的统计项
	 */
	private List<Entry> entries = new ArrayList<Entry>();
	
	public static class Entry {

		/**显示的term*/
		private String term;
		
		private long count;
		
		private int sort;
		
		/**原始的term*/
		private String originalTerm;
		
		public Entry(){}
		
		public Entry(String term,long count){
			this.term = term;
			this.count = count;
		}
		
		public Entry(String term,long count,String originalTerm){
			this(term,count);
			this.originalTerm = originalTerm;
		}

		public String getTerm() {
			return term;
		}

		public void setTerm(String term) {
			this.term = term;
		}

		public long getCount() {
			return count;
		}

		public void setCount(long count) {
			this.count = count;
		}

		public String getOriginalTerm() {
			return originalTerm;
		}

		public void setOriginalTerm(String originalTerm) {
			this.originalTerm = originalTerm;
		}

		public int getSort() {
			return sort;
		}

		public void setSort(int sort) {
			this.sort = sort;
		}
		
    }
	
	public void addEntry(Entry entry){
		this.entries.add(entry);
	}
	
	public void addEntry(String term,long count){
		Entry entry = new Entry(term,count);
		this.entries.add(entry);
	}
	
	public void addEntry(String term,long count,String originalTerm){
		Entry entry = new Entry(term,count,originalTerm);
		this.entries.add(entry);
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

}

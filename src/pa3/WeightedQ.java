package pa3;

import java.util.*;
import java.util.Map.*;

public class WeightedQ {

	public Map<String, Integer> queue;

	WeightedQ() {
		this.queue = new LinkedHashMap<String, Integer>();
	}

	public void add(String key, Integer value) {
		this.queue.put(key, value);
		if (queue.size() > 1) {
			

		}

	}

	public Entry<String, Integer> extract() {
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(this.queue.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Entry tuple = list.get(0);
		String key = (String) tuple.getKey();
		this.queue.remove(key);

		return tuple;
	}

}

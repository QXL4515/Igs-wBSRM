package www.hhu.edu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class TFIDF_ComputeWi_C0 implements TFIDF_ComputeWi {
	public HashMap<HashMap<String, String>, Double> computeWi(
			HashMap<HashMap<String, String>, Integer> ll2Num/* 所有国家组合的总数 */,
			HashMap<HashMap<String, String>, Integer> ll2C0/* 所有国家组合所有属于c0的 */,
			double preset_value, int nCi, int n) {
		
		System.out.println("Begin to computeWi_C0...");
		long begin = System.currentTimeMillis();

		HashMap<HashMap<String, String>, Double> ll2W0Temp = new HashMap<HashMap<String, String>, Double>();
		Iterator<Entry<HashMap<String, String>, Integer>> numIterator = ll2Num
				.entrySet().iterator();
		while (numIterator.hasNext()) {
			Entry<HashMap<String, String>, Integer> entry = numIterator.next();
			if (ll2C0.containsKey(entry.getKey())) {
				int nCiRi = ll2C0.get(entry.getKey());
				int nRi = entry.getValue();
				double wi = nCiRi * 1.0 / nCi * Math.log(n / nRi);
				ll2W0Temp.put(entry.getKey(), wi);
			} else {
				ll2W0Temp.put(entry.getKey(), preset_value);
			}
		}
		long end = System.currentTimeMillis();
		System.out.println("End to computeWi_C0, takes"	+ new TFIDF_ComputeTime().computeTime(begin, end)+ "1000 ms...");
		return ll2W0Temp;
	}
}

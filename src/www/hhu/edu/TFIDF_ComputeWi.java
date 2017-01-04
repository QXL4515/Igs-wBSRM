package www.hhu.edu;

import java.util.HashMap;

public interface TFIDF_ComputeWi {
	public HashMap<HashMap<String, String>, Double> computeWi(
			HashMap<HashMap<String, String>, Integer> ll2Num,
			HashMap<HashMap<String, String>, Integer> ll2Ci,
			double preset_value, int nCi, int n);
}

package www.hhu.edu;

import java.util.HashMap;

public interface TFIDF_UpdateWi {
	public double computeWi(
			HashMap<String, String> ll,
			HashMap<HashMap<String, String>, Integer> ll2Num,
			HashMap<HashMap<String, String>, Integer> ll2C0,
			HashMap<HashMap<String, String>, Integer> ll2C1,
			double preset_value, int nC0, int nC1,int n);

}

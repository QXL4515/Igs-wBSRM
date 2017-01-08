package www.hhu.edu;

import java.util.HashMap;

public class TFIDF_UpdateWi_C0 implements TFIDF_UpdateWi{

	@Override
	public double computeWi(
			HashMap<String, String> ll,
			HashMap<HashMap<String, String>, Integer> ll2Num,
			HashMap<HashMap<String, String>, Integer> ll2C0,
			HashMap<HashMap<String, String>, Integer> ll2C1,
			double preset_value, int nC00,int nC11, int nn) {
		double nC1Ri;
		double nC0 =nC00;
		double nC1 = nC11;
		double n = nn;
		if(ll2C1.containsKey(ll)){
			nC1Ri = ll2C1.get(ll);
		}else{
			nC1Ri = 0;
		}
		if (ll2C0.containsKey(ll)) {
			double nC0Ri = ll2C0.get(ll);
			double nRi = ll2Num.get(ll);
			double wi = nC0Ri * 1.0 / nC0 * Math.log(n / nRi)*
					(
						-nC0/n*(Math.log(nC0/n)/Math.log(2.0))-nC1/n*(Math.log(nC1/n)/Math.log(2.0))
						+nRi/n*
						((nC1Ri+1)/(nRi+1)*(Math.log((nC1Ri+1)/(nRi+1))/Math.log(2.0))+(nC0Ri+1)/(nRi+1)*(Math.log((nC0Ri+1)/(nRi+1))/Math.log(2.0)))
						+(n-nRi)/n*
						((nC1-nC1Ri+1)/(n-nRi+1)*(Math.log((nC1-nC1Ri+1)/(n-nRi+1))/Math.log(2.0))+(nC0-nC0Ri+1)/(n-nRi+1)*(Math.log((nC0-nC0Ri+1)/(n-nRi+1))/Math.log(2.0)))
					);								//此处+1做拉普拉斯平滑处理

			return wi;
		} else {
			return preset_value;
		}
	
	}
	
}

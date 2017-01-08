package www.hhu.edu;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class TFIDF_ComputePrePro_C0 implements TFIDF_ComputePrePro_CX{
	public double computePrePro_CX(double plC0, int x, int y, ArrayList<TFIDF_UserBean> userList, 
			ArrayList<TFIDF_WebServiceBean> webServiceList,HashMap<HashMap<String, String>, Double> ll2Wi_C0, int[][] tp){
		//计算先验概率
		int temp = tp[x][y];
		String E = "YorN_QoS 0-1 value: " + temp;
		double pXiC0 = Math.pow(plC0, temp) * Math.pow(1 - plC0, (1 - temp));
		HashMap<String, String> ll = new HashMap<String, String>();
		ll.put(userList.get(x).getNation(), webServiceList.get(y).getNation());
		double wi_c0 = ll2Wi_C0.get(ll) ;
		double wi_pXiC0=wi_c0*Math.log(1+pXiC0);
		return wi_pXiC0;
	}
}

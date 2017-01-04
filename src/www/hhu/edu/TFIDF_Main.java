package www.hhu.edu;

import java.awt.Button;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.omg.CORBA.PUBLIC_MEMBER;

public class TFIDF_Main {
	// 目标β值
	private static double BETA = 0.46;										// BETA为QoS属性标准。即设置的一个基线——满足QoS要求的比例数
	// 与这个基线 进行比较
	private static double QoS_VALUE = 10.0;									// QoS属性值要求
	private static final int DISTANCE = 1000;
	private static final double PERSET_VALUE = 0.0;
	private static String USERINFORMATION_DATA_PATH = "E:/TFIDF_Data/userlist.txt";
	private static String WSINFORMATION_DATA_PATH = "E:/TFIDF_Data/wslist.txt";
	private static String TPINFORMATION_DATA_PATH = "E:/TFIDF_Data/tpmatrix.txt";
	private static String RTINFORMATION_DATA_PATH = "E:/TFIDF_Data/rtmatrix.txt";
	private static String OUT_PATH = "E:/ProjectIMP/Final/time";
	private static ArrayList<TFIDF_UserBean> userList = new ArrayList<TFIDF_UserBean>();
	private static ArrayList<TFIDF_WebServiceBean> webServiceList = new ArrayList<TFIDF_WebServiceBean>();
	private static HashMap<HashMap<String, String>, Integer> ll2Num = new HashMap<HashMap<String, String>, Integer>();// 记录各不同国家组合的总数
	private static HashMap<HashMap<String, String>, Integer> ll2C0 = new HashMap<HashMap<String, String>, Integer>();// 记录各不同国家组合的C0个数
	private static HashMap<HashMap<String, String>, Integer> ll2C1 = new HashMap<HashMap<String, String>, Integer>();// 记录各不同国家组合的C1个数
	private static HashMap<HashMap<String, String>, Double> ll2Wi_C0 = new HashMap<HashMap<String, String>, Double>();// 记录各不同国家组合的Wi_C0
	private static HashMap<HashMap<String, String>, Double> ll2Wi_C1 = new HashMap<HashMap<String, String>, Double>();// 记录各不同国家组合的Wi_C1
	private static double[][] rtData, tpData;
	
	private static int shortMonlength = 200;
	private static Vector<Double> record_NQoS = new Vector<Double>();
	private static Vector<Double> recordpreProC0 = new Vector<Double>();;
	private static Vector<Double> recordpreProC1 = new Vector<Double>();;
	private static Vector<Integer> YorN = new Vector<Integer>();

	private static int[][] tp, rt; 					// 记录吞吐量、响应时间对应的0-1值
	private static int nC0Xl, nC1Xl, nC0, nC1, n;	// Xl 表示 Xk = 1;
													// nC0、nC1指的是通过先验概率的n——即计算先验概率的分子。
													// n为当前总样本数
	static int countQoS = 0;						// 满足QoS要求的样本数

	private static double prePro_C0 = 1.0;
	private static double prePro_C1 = 1.0;

	private static Frame f = null;
	private static Button but = null;
	private static Button but1 = null;
	private static Button but2 = null;
	private static Button but3 = null;
	private static Button but4 = null;
	private static Button startButton = null;
	private static FileDialog openDia = null;
	private static JFileChooser openDir = null;
	private static TextField BetaTF = null;
	private static TextField QoSTF = null;
	private static TextArea outTA = null;
	private static MenuBar infoBar = null;
	private static Menu infoM = null;
	private static MenuItem infoIt = null;
	private static Dialog authorDia = null;
	private static Label authorLab = null;
	private static Label authorLab1 = null;
	private static Label authorLab2 = null;

	public TFIDF_Main() {
		init();
	}

	public static void init() {
		f = new Frame("Updated_TFIDF");

		f.setBounds(300, 100, 600, 360);
		f.setLayout(new FlowLayout());

		infoBar = new MenuBar();
		infoM = new Menu("Help");
		infoIt = new MenuItem("About...");
		but = new Button("Open_UserList");
		but1 = new Button("Open_WSList");
		but2 = new Button("Open_TPInfo");
		but3 = new Button("Open_RTInfo");
		but4 = new Button("Out_Path");
		authorLab1 = new Label();
		authorLab1.setAlignment(Label.RIGHT);
		authorLab1.setText("To Input Predefine BETA:");
		BetaTF = new TextField();
		authorLab2 = new Label();
		authorLab2.setAlignment(Label.RIGHT);
		authorLab2.setText("To Input Standard QoS_VALUE:");
		QoSTF = new TextField();
		QoSTF.setSize(30, 10);
		startButton = new Button("Start_Compute");
		outTA = new TextArea();

		f.setMenuBar(infoBar);
		openDia = new FileDialog(f, "USERINFORMATION_DATA To Choose", FileDialog.LOAD);

		f.add(but);
		f.add(but1);
		f.add(but2);
		f.add(but3);
		f.add(but4);
		f.add(startButton);
		f.add(authorLab1);
		f.add(BetaTF);
		f.add(authorLab2);
		f.add(QoSTF);
		f.add(outTA);

		infoBar.add(infoM);
		infoM.add(infoIt);

		myEvent();

		f.setVisible(true);

	}

	private static void myEvent() {
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		infoIt.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				authorDia = new Dialog(f, "About:", false);
				authorLab = new Label();
				authorLab.setAlignment(Label.CENTER);
				authorLab.setText("@author ZH-He          @version 1.0.0");
				authorDia.add(authorLab);
				authorDia.setBounds(400, 200, 300, 100);
				authorDia.setVisible(true);
				authorDia.addWindowListener(new WindowAdapter() {
					public void windowClosing(WindowEvent e) {
						authorDia.setVisible(false);
					}
				});
			}
		});

		but.addActionListener(new UserLis());
		but1.addActionListener(new WSLis());
		but2.addActionListener(new TPLis());
		but3.addActionListener(new RTLis());
		but4.addActionListener(new OutKLis());
		BetaTF.addActionListener(new BetaTFLis());
		QoSTF.addActionListener(new QoSTFLis());
		startButton.addActionListener(new StartLis());
	}

	public static class UserLis implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			openDia.setVisible(true);

			String dirPath = openDia.getDirectory();
			String fileName = openDia.getFile();

			String wholePath = dirPath + fileName;

			TFIDF_Main.USERINFORMATION_DATA_PATH = wholePath;
		}
	}

	public static class WSLis implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			openDia.setVisible(true);

			String dirPath = openDia.getDirectory();
			String fileName = openDia.getFile();

			String wholePath = dirPath + fileName;

			TFIDF_Main.WSINFORMATION_DATA_PATH = wholePath;
		}
	}

	public static class TPLis implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			openDia.setVisible(true);

			String dirPath = openDia.getDirectory();
			String fileName = openDia.getFile();

			String wholePath = dirPath + fileName;

			TFIDF_Main.TPINFORMATION_DATA_PATH = wholePath;
		}
	}

	public static class RTLis implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			openDia.setVisible(true);

			String dirPath = openDia.getDirectory();
			String fileName = openDia.getFile();

			String wholePath = dirPath + fileName;

			TFIDF_Main.RTINFORMATION_DATA_PATH = wholePath;
		}
	}

	public static class OutKLis implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			openDir = new JFileChooser();

			openDir.setFileSelectionMode(openDir.DIRECTORIES_ONLY);

			openDir.showOpenDialog(but4);

			String dirPath = openDir.getSelectedFile().getPath();

			TFIDF_Main.OUT_PATH = dirPath;
		}
	}

	public static class BetaTFLis implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			BetaTF.select(0, BetaTF.getSelectionEnd());

			TFIDF_Main.BETA = Double.parseDouble(BetaTF.getSelectedText());
		}
	}

	public static class QoSTFLis implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			QoSTF.select(0, QoSTF.getSelectionEnd());
			TFIDF_Main.QoS_VALUE = Double.parseDouble(QoSTF.getSelectedText());
		}
	}

	public static class StartLis implements ActionListener {

		public void actionPerformed(ActionEvent ae) {

			nC0 = 0;
			nC1 = 0;
			long begin_n = 0;
			long end_n = 0;
			int y1, y2, y3, y4;
			readData(USERINFORMATION_DATA_PATH, WSINFORMATION_DATA_PATH, RTINFORMATION_DATA_PATH, TPINFORMATION_DATA_PATH);// 读取文件数据
			int[] a = traversalTPMatrix(userList, webServiceList, tpData);// 处理tp数据，如果需要处理rt数据则把二维数组换成rtdata
			y1 = a[0];
			y2 = a[1];
			y3 = a[2];
			y4 = a[3];
			ll2Wi_C0 = new TFIDF_ComputeWi_C0().computeWi(ll2Num, ll2C0, PERSET_VALUE, nC0, n);// 计算Wi_C0
			ll2Wi_C1 = new TFIDF_ComputeWi_C1().computeWi(ll2Num, ll2C1, PERSET_VALUE, nC1, n);// 计算Wi_C1
			updateWeight(y1, y2, y3, y4, tpData);
			// try {
			// FileWriter fw = new FileWriter(OUT_PATH + "/ll2Wi_C0.txt", true);
			// FileWriter fw1 = new FileWriter(OUT_PATH + "/ll2Wi_C1.txt",
			// true);
			// BufferedWriter bw = new BufferedWriter(fw);
			// BufferedWriter bw1 = new BufferedWriter(fw1);
			// Set<HashMap<String, String>> s = ll2Wi_C0.keySet();
			// Set<HashMap<String, String>> s1 = ll2Wi_C1.keySet();
			// Iterator<HashMap<String, String>> i0 = s.iterator();
			// Iterator<HashMap<String, String>> i1 = s1.iterator();
			// while (i0.hasNext()) {
			// String G = "ll2Wi_C0 = " + ll2Wi_C0.get(i0.next());
			// bw.write(G);
			// bw.newLine();
			// bw.flush();
			// }
			// while (i1.hasNext()) {
			// String G = "ll2Wi_C1 = " + ll2Wi_C1.get(i1.next());
			// bw1.write(G);
			// bw1.newLine();
			// bw1.flush();
			// }
			// fw.close();
			// fw1.close();
			// bw.close();
			// bw1.close();
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// try {
			// FileWriter fw0 = new FileWriter(OUT_PATH + "/ll2C0.txt", true);
			// FileWriter fw3 = new FileWriter(OUT_PATH + "/ll2C1.txt", true);
			// BufferedWriter bw0 = new BufferedWriter(fw0);
			// BufferedWriter bw3 = new BufferedWriter(fw3);
			// Set<HashMap<String, String>> s0 = ll2C0.keySet();
			// Set<HashMap<String, String>> s3 = ll2C1.keySet();
			// Iterator<HashMap<String, String>> i0 = s0.iterator();
			// Iterator<HashMap<String, String>> i3 = s3.iterator();
			// while (i0.hasNext()) {
			// HashMap<String, String> td = i0.next();
			// // Collection<String> c = td.values();
			// // String[] s = (String[]) c.toArray();
			// String G2 = ll2C0.get(td).toString();
			// bw0.write(G2);
			// bw0.newLine();
			// bw0.flush();
			// }
			// while (i3.hasNext()) {
			// HashMap<String, String> td1 = i3.next();
			// String G3 =ll2C1.get(td1).toString();
			// bw3.write(G3);
			// bw3.newLine();
			// bw3.flush();
			// }
			// fw0.close();
			// fw3.close();
			// bw0.close();
			// bw3.close();
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			double plC0 = new TFIDF_ComputePlC0().computePlCX(nC0Xl, nC0);// 经验贝叶斯估计
			double plC1 = new TFIDF_ComputePlC1().computePlCX(nC1Xl, nC1);// 计算P
			
			// outTA.append("plC0" + plC0 + "\r\n");
			// outTA.append("plC1" + plC1 + "\r\n");
			
			outTA.append("The output data is right in the path：" + OUT_PATH + "/test_data.txt" + "\r\n");

			nC0 = 0;
			nC1 = 0;
			int count = 0; // 循环中目前为止 的样本数量
			int x = 0; // 循环中目前为止 小于给定QoS值的样本数量
			double K;
			String R0;
			String R1;
			int i, m, q, p;
			begin_n = System.currentTimeMillis();
			for (i = 0; i < DISTANCE; i++) {
				for (m = i; m < tpData.length; m += DISTANCE) {
					for (q = 0; q < DISTANCE; q++) {
						for (p = q; p < tpData[0].length; p += DISTANCE) {
							count++;
							if (count == 3501) {
								end_n = System.currentTimeMillis();
								System.out.println("begin_n: " + begin_n);
								System.out.println("end_n: " + end_n);
								outTA.append("3500个样本所用时间" + (end_n - begin_n) + "\r\n");
								return;
							}

							record_NQoS.add(tpData[m][p]);

							if (tpData[m][p] <= QoS_VALUE) {
								x++;
							}

							double aftPro_C0;
							double aftPro_C1;

							if (count > shortMonlength) {
								double abandon = record_NQoS.get(count - shortMonlength);
								if (abandon <= QoS_VALUE) {
									x--;
								}

								double c = x * 1.0 / shortMonlength;
								if (c >= BETA) {
									YorN.add(1);
									nC0++;
								} else {
									YorN.add(0);
									nC1++;
								}
								aftPro_C0 = computeAftPro_C0(plC0, m, p, userList, webServiceList, ll2Wi_C0, tpData, x, count, tp);
								aftPro_C1 = computeAftPro_C1(plC1, m, p, userList, webServiceList, ll2Wi_C1, tpData, x, count, tp);
								K = Math.pow(Math.abs(aftPro_C0), BETA) / Math.pow(Math.abs(aftPro_C1), BETA);
							} else {
								double c = x * 1.0 / count;
								if (c >= BETA) {
									YorN.add(1);
									nC0++;
								} else {
									YorN.add(0);
									nC1++;
								}
								aftPro_C0 = computeAftPro_C0(plC0, m, p, userList, webServiceList, ll2Wi_C0, tpData, x, count, tp);
								aftPro_C1 = computeAftPro_C1(plC1, m, p, userList, webServiceList, ll2Wi_C1, tpData, x, count, tp);
								K = Math.pow(Math.abs(aftPro_C0), BETA) / Math.pow(Math.abs(aftPro_C1), BETA);
							}

							try {

								FileWriter fw = new FileWriter(OUT_PATH + "/test_K.txt", true);
								BufferedWriter bw = new BufferedWriter(fw);

								String G = "K = " + K;
								bw.write(G);
								bw.newLine();
								bw.flush();
								fw.close();
								bw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}

							R0 = "******* aftPro_C0=  " + aftPro_C0 + " *******";
							R1 = "******* aftPro_C1=  " + aftPro_C1 + " *******";
							try {
								FileWriter fw = new FileWriter(OUT_PATH + "/test_data.txt", true);
								BufferedWriter bw = new BufferedWriter(fw);
								bw.write(R0);
								bw.newLine();
								bw.write(R1);
								bw.newLine();
								bw.flush();
								fw.close();
								bw.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							if (K > 1) {
								try {
									FileWriter fw = new FileWriter(OUT_PATH + "/test_YorN.txt", true);
									BufferedWriter bw = new BufferedWriter(fw);
									String s = "1";
									bw.write(s);
									bw.newLine();
									bw.flush();
									fw.close();
									bw.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (K < 1) {
								try {
									FileWriter fw = new FileWriter(OUT_PATH + "/test_YorN.txt", true);
									BufferedWriter bw = new BufferedWriter(fw);
									String s = "-1";
									bw.write(s);
									bw.newLine();
									bw.flush();
									fw.close();
									bw.close();
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}

			System.out.println("我做完了");
		}

		public void updateWeight(int y1, int y2, int y3, int y4,
				double[][] matrix) {
			double c = 0.0;
			int i, m, q, p;
			double Wi_C0Current, Wi_C1Current;
			int flag = 0;
			for (i = y1; i < DISTANCE; i++) {
				for (m = i; m < matrix.length; m += DISTANCE) {
					for (q = 0; q < DISTANCE; q++) {
						for (p = q; p < matrix[0].length; p += DISTANCE) {

							if (i == y1 && m == y2 && q == y3 && p == (y4 + DISTANCE))
								flag = 1;

							if (flag == 0)
								continue;

							n++;

//							if ((n > 1200 && n < 1700) || (n > 2000 && n < 2500)) {
//								matrix[m][p] = 20.0;
//								tpData[m][p] = 20.0;
//							} else if (n % 2 == 0) {
//								matrix[m][p] = 20.0;
//								tpData[m][p] = 20.0;
//							} else {
//								matrix[m][p] = 3.0;
//								tpData[m][p] = 3.0;
//							}

							TFIDF_UserBean userInformation = userList.get(m);
							TFIDF_WebServiceBean webServiceInformation = webServiceList
									.get(p);
							HashMap<String, String> ll = new HashMap<String, String>();
							ll.put(userInformation.getNation(),
									webServiceInformation.getNation());
							if (ll2Num.containsKey(ll)) {
								ll2Num.put(ll, ((Integer) ll2Num.get(ll)) + 1);
							} else {
								ll2Num.put(ll, 1);
							}
							if (matrix[m][p] <= QoS_VALUE) {
								countQoS++;
								tp[m][p] = 1;
								nC0Xl++;
								nC1Xl++;
								c = countQoS * 1.0 / n;
								if (c >= BETA) { /* 为了把功能分离开吧 */// 达不到QoS值标准的数目占目前总数的比值大于等于我们约定的临界值β，那么显然不落在c1中。
									nC1Xl--;
								} else {
									nC0Xl--;
								}
							}
							c = countQoS * 1.0 / n;
							if (c >= BETA) { /* 为了把功能分离开吧 */
								if (ll2C0.containsKey(ll)) {
									ll2C0.put(ll, ((Integer) ll2C0.get(ll)) + 1);
								} else {
									ll2C0.put(ll, 1);
								}
								nC0++;
							} else {
								if (ll2C1.containsKey(ll)) {
									ll2C1.put(ll, ((Integer) ll2C1.get(ll)) + 1);
								} else {
									ll2C1.put(ll, 1);
								}
								nC1++;
							}
							// System.out.println("nCo: "+nC0+" nC1: "+nC1+" n: "+n);

							Wi_C0Current = new TFIDF_UpdateWi_C0().computeWi(
									ll, ll2Num, ll2C0, ll2C1, PERSET_VALUE,
									nC0, nC1, n);// 更新Wi_C0
							Wi_C1Current = new TFIDF_UpdateWi_C1().computeWi(
									ll, ll2Num, ll2C0, ll2C1, PERSET_VALUE,
									nC0, nC1, n);// 更新Wi_C1

							ll2Wi_C0.put(ll, Wi_C0Current);
							ll2Wi_C1.put(ll, Wi_C1Current);

						}
					}
				}
			}
			return;
		}
	}

	/**
	 * @param args
	 */

	public static void main(String[] args) {

		new TFIDF_Main();

	}

	public static Long[] readData(String userPath, String webSevicePath,
			String RTPath, String TPPath) {

		Long[] time = new Long[4];
		// 读取userList数据
		userList = new TFIDF_ReadUserInformationDataFromTxt(userPath)
				.readData();
		System.out.println("Read user information success...");
		// 读取webService数据
		webServiceList = new TFIDF_ReadWebServiceInformationDataFromTxt(
				webSevicePath).readData();
		System.out.println("Read web service information success...");

		rtData = new double[userList.size()][webServiceList.size()];
		tpData = new double[userList.size()][webServiceList.size()];
		rt = new int[userList.size()][webServiceList.size()];
		tp = new int[userList.size()][webServiceList.size()];
		// 读取rt数据
		System.out.println("Begin to read RT matrix...");
		time[0] = System.currentTimeMillis();
		rtData = new TFIDF_ReadRTDataFromTxt(RTINFORMATION_DATA_PATH,
				userList.size(), webServiceList.size()).readData();
		time[1] = System.currentTimeMillis();
		System.out.println("End to read RT matrix, takes"
				+ new TFIDF_ComputeTime().computeTime(time[0], time[1])
				+ "ms...");

		// 读取tp数据
		System.out.println("Begin to read PT matrix...");
		time[2] = System.currentTimeMillis();
		tpData = new TFIDF_ReadTPDataFromTxt(TPINFORMATION_DATA_PATH,
				userList.size(), webServiceList.size()).readData();
		time[3] = System.currentTimeMillis();
		System.out.println("End to read PT matrix, takes"
				+ new TFIDF_ComputeTime().computeTime(time[2], time[3])
				+ "ms...");
		return time;
	}

	/**
	 * @param userList
	 * @param webServiceList
	 * @param matrix
	 */
	public static int[] traversalTPMatrix(ArrayList<TFIDF_UserBean> userList,
			ArrayList<TFIDF_WebServiceBean> webServiceList, double[][] matrix) {
		System.out.println("Begin to traversalMatrix...");
		// System.out.println("matrix.length------"+matrix.length);
		long begin = System.currentTimeMillis();
		// int n = 0;

		double c = 0.0;
		nC0Xl = 0;
		nC1Xl = 0;
		nC0 = 0;
		nC1 = 0;
		int i, m, q, p;
		for (i = 0; i < DISTANCE; i++) {
			for (m = i; m < matrix.length; m += DISTANCE) {
				for (q = 0; q < DISTANCE; q++) {
					for (p = q; p < matrix[0].length; p += DISTANCE) {
						n++;

						if (n == 1000) {
							TFIDF_UserBean userInformation = userList.get(m);
							TFIDF_WebServiceBean webServiceInformation = webServiceList
									.get(p);
							HashMap<String, String> ll = new HashMap<String, String>();
							ll.put(userInformation.getNation(),
									webServiceInformation.getNation());
							if (ll2Num.containsKey(ll)) {
								ll2Num.put(ll, ((Integer) ll2Num.get(ll)) + 1);
							} else {
								ll2Num.put(ll, 1);
							}
							if (matrix[m][p] <= QoS_VALUE) {
								countQoS++;
								tp[m][p] = 1;
								nC0Xl++;
								nC1Xl++;
								c = countQoS * 1.0 / n;
								if (c >= BETA) { /* 为了把功能分离开吧 */// 达不到QoS值标准的数目占目前总数的比值大于等于我们约定的临界值β，那么显然不落在c1中。
									nC1Xl--;
								} else {
									nC0Xl--;
								}
							}
							c = countQoS * 1.0 / n;
							if (c >= BETA) { /* 为了把功能分离开吧 */
								if (ll2C0.containsKey(ll)) {
									ll2C0.put(ll, ((Integer) ll2C0.get(ll)) + 1);
								} else {
									ll2C0.put(ll, 1);
								}
								nC0++;
							} else {
								if (ll2C1.containsKey(ll)) {
									ll2C1.put(ll, ((Integer) ll2C1.get(ll)) + 1);
								} else {
									ll2C1.put(ll, 1);
								}
								nC1++;
							}

							long end = System.currentTimeMillis();
							System.out.println("End to traversalMatrix, takes"
									+ new TFIDF_ComputeTime().computeTime(
											begin, end) + "ms...");
							int[] a = new int[4];
							a[0] = i;
							a[1] = m;
							a[2] = q;
							a[3] = p;
							System.out.println("i: " + i + " m: " + m + " q: "
									+ q + " p: " + p + "...");
							System.out.println("i: " + a[0] + " m: " + a[1]
									+ " q: " + a[2] + " p: " + a[3] + "...");
							System.out.println("now BETA:"
									+ (countQoS * 1.0 / n));
							return a;
						}
						TFIDF_UserBean userInformation = userList.get(m);
						TFIDF_WebServiceBean webServiceInformation = webServiceList
								.get(p);
						HashMap<String, String> ll = new HashMap<String, String>();
						ll.put(userInformation.getNation(),
								webServiceInformation.getNation());
						if (ll2Num.containsKey(ll)) {
							ll2Num.put(ll, ((Integer) ll2Num.get(ll)) + 1);
						} else {
							ll2Num.put(ll, 1);
						}
						if (matrix[m][p] <= QoS_VALUE) {
							countQoS++;
							tp[m][p] = 1;
							nC0Xl++;
							nC1Xl++;
							c = countQoS * 1.0 / n;
							if (c >= BETA) { /* 为了把功能分离开吧 */// 达不到QoS值标准的数目占目前总数的比值大于等于我们约定的临界值β，那么显然不落在c1中。
								nC1Xl--;
							} else {
								nC0Xl--;
							}
						}
						c = countQoS * 1.0 / n;
						if (c >= BETA) { /* 为了把功能分离开吧 */
							if (ll2C0.containsKey(ll)) {
								ll2C0.put(ll, ((Integer) ll2C0.get(ll)) + 1);
							} else {
								ll2C0.put(ll, 1);
							}
							nC0++;
						} else {
							if (ll2C1.containsKey(ll)) {
								ll2C1.put(ll, ((Integer) ll2C1.get(ll)) + 1);
							} else {
								ll2C1.put(ll, 1);
							}
							nC1++;
						}
					}
				}
			}
		}
		return null;

	}

	public static double computePro_C0(double[][] tpData, int x, int n) {
		// 根据C0计算似然概率，为后面计算后验概率做准备
		if (n > shortMonlength) {
			if (YorN.get(n - shortMonlength - 1) == 1) {
				nC0--;
			}
			double pro_C0 = (nC0 * 1.0 + 1) / shortMonlength;
			return pro_C0;
		} else {
			double pro_C0 = (nC0 * 1.0 + 1) / (n + 2);
			return pro_C0;
		}
	}

	public static double computePro_C1(double[][] tpData, int x, int n) {
		// 根据C1计算似然概率，为后面计算后验概率做准备
		if (n > shortMonlength) {
			if (YorN.get(n - shortMonlength - 1) == 0) {
				nC1--;
			}
			double pro_C1 = (nC1 * 1.0 + 1) / shortMonlength;
			return pro_C1;
		} else {
			double pro_C1 = (nC1 * 1.0 + 1) / (n + 2);
			return pro_C1;
		}
	}

	public static double computeAftPro_C0(double plC0, int a, int b,
			ArrayList<TFIDF_UserBean> userList,
			ArrayList<TFIDF_WebServiceBean> webServiceList,
			HashMap<HashMap<String, String>, Double> ll2Wi_C0,
			double[][] tpData, int x, int n, int[][] tp) {

		double pro_C0 = computePro_C0(tpData, x, n);

		double RpreProC0 = new TFIDF_ComputePrePro_C0().computePrePro_CX(plC0,
				a, b, userList, webServiceList, ll2Wi_C0, tp);
		recordpreProC0.add(RpreProC0);
		if (n > shortMonlength) {
			prePro_C0 = prePro_C0 + RpreProC0 - recordpreProC0.get(n - shortMonlength - 1);
		} else {
			prePro_C0 = prePro_C0 + RpreProC0;
		}

		double pC0X = new TFIDF_ComputePCiX().computePCiX(pro_C0, prePro_C0);
		return pC0X;

	}

	public static double computeAftPro_C1(double plC1, int a, int b,
			ArrayList<TFIDF_UserBean> userList,
			ArrayList<TFIDF_WebServiceBean> webServiceList,
			HashMap<HashMap<String, String>, Double> ll2Wi_C1,
			double[][] tpData, int x, int n, int[][] tp) {

		double pro_C1 = computePro_C1(tpData, x, n);

		double RprePro_C1 = new TFIDF_ComputePrePro_C1().computePrePro_CX(plC1,
				a, b, userList, webServiceList, ll2Wi_C1, tp);
		recordpreProC1.add(RprePro_C1);
		if (n > shortMonlength) {
			prePro_C1 = prePro_C1 + RprePro_C1 - recordpreProC1.get(n - shortMonlength - 1);
		} else {
			prePro_C1 = prePro_C1 + RprePro_C1;
		}
		double pC1X = new TFIDF_ComputePCiX().computePCiX(pro_C1, prePro_C1);

		return pC1X;
	}

}

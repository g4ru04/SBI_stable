package tw.com.sbi.dataupdate.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DownloadFiles {
	private static final Logger logger = LogManager.getLogger(DownloadFiles.class);
	// 起始寫給高工局流量使用
	// "http://tisvcloud.freeway.gov.tw/history/TDCS/M03A/M03A_20170608.tar.gz"
	// 目前只有解壓縮 tar.gz檔 有空再解決
	public static boolean downlodFileSuccess(String downloadStr, String downloadPosition) {
		// 目前暫時不能用https的 有空再解決
		try {
			FileUtils.copyURLToFile(new URL(downloadStr), new File(downloadPosition));
		} catch (Exception e) {
			logger.debug("full-range-log",e);
			try {
				java.util.concurrent.TimeUnit.SECONDS.sleep(1);
				FileUtils.copyURLToFile(new URL(downloadStr), new File(downloadPosition));
			} catch (Exception e1) {
				logger.debug("full-range-log",e1);
				try {
					java.util.concurrent.TimeUnit.SECONDS.sleep(1);
					FileUtils.copyURLToFile(new URL(downloadStr), new File(downloadPosition));
				} catch (Exception e2) {
					logger.debug("full-range-log",e2);
					logger.info("嘗試三次下載檔案依然無效: " + e.toString());
//					e.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	public static void collectAllFiles(String ori_folder, String dest_folder) {
		File dir = new File(ori_folder);
		if (dir.isFile()) {
			copyFile(ori_folder, dest_folder);
		}
		String[] children = dir.list();
		if (children == null) {
			return;
		} else {
			for (int i = 0; i < children.length; i++) {
				String filename = children[i];
				collectAllFiles(dir.getAbsolutePath() + "/" + filename, dest_folder);
			}
		}
	}

	public static boolean isValidURL(String urlStr) {
		URL url;
		try {
			url = new URL(urlStr);
			InputStream in = url.openStream();
			in.close();
		} catch (Exception e1) {
//			e1.printStackTrace();
			logger.debug("URL無效: " + urlStr);
			url = null;
			return false;
		}
		return true;
	}

	public static String getWantedFolder(String folder, String folderNameContain) {
		File dir = new File(folder);
		String[] children = dir.list();
		if (children == null) {
			return "";
		} else {
			for (int i = 0; i < children.length; i++) {
				String filename = children[i];
				if (folderNameContain.equals(filename)) {
					return new File(dir.getAbsolutePath() + "/" + filename).getAbsolutePath();
				}
				String recursive_ans = getWantedFolder(dir.getAbsolutePath() + "/" + filename, folderNameContain);
				if (recursive_ans != "") {
					return recursive_ans;
				}
			}
		}
		return "";
	}

	public static boolean deleteFolder(String folder) {
		File file = new File(folder);
		if (!file.exists()) {
			return true;
		}
		if (!file.isDirectory()) {
			return true;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (folder.endsWith(File.separator)) {
				temp = new File(folder + tempList[i]);
			} else {
				temp = new File(folder + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				temp.delete();
			}
			if (temp.isDirectory()) {
				deleteFolder(folder + "/" + tempList[i]);// 先刪除資料夾裡面的檔
				delFolder(folder + "/" + tempList[i]);// 再刪除空資料夾
			}
		}
		return true;
	}

	public static void delFolder(String folderPath) {
		try {
			deleteFolder(folderPath); // 刪除完裡面所有內容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			myFilePath.delete(); // 刪除空資料夾
		} catch (Exception e) {
			logger.debug("刪除資料夾操作出錯: ");
			e.printStackTrace();
		}
	}

	public static void copyFolderTo(String oldPath, String newPath) {
		try {
			(new File(newPath)).mkdirs(); // 如果資料夾不存在則建立新資料夾
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile()) {
					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}
					output.flush();
					output.close();
					input.close();
				}
				if (temp.isDirectory()) {// 如果是子資料夾
					copyFolderTo(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			logger.debug("複製整個資料夾內容操作出錯");
			e.printStackTrace();
		}
	}

	public static void copyFile(String oldPath, String folder) {
		
		try {
			File f = new File(folder);
			if (!f.exists() || !f.isDirectory()) {
			   UnzipFiles.createDirectory(folder, null);
			}
			
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			String newPath = folder+"/"+oldfile.getName();
			
			
			if (oldfile.exists()) { // 檔存在時
				InputStream inStream = new FileInputStream(oldPath);// 讀入原檔
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 位元組數 檔案大小
//					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			logger.debug("複製單個檔操作出錯");
			e.printStackTrace();

		}

	}
}

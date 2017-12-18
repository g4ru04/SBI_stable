package tw.com.sbi.record.controller;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
//import com.itextpdf.text.Chunk;
//import org.apache.commons.lang.StringEscapeUtils;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.Image;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfPageEventHelper;
//import com.itextpdf.text.pdf.PdfTemplate;
//import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;

import tw.com.sbi.common.controller.CommonMethod;
import tw.com.sbi.scenariojob.controller.ScenarioJob.*;
import tw.com.sbi.vo.RecordVO;
import tw.com.sbi.vo.ScenarioJobVO;
import tw.com.sbi.vo.ScenarioResultVO;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;

public class Record extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(Record.class);
	
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/text");
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		String action = null2str(request.getParameter("action"));
		logger.debug("action: "+action);
		if ("dataURL_to_PNG".equals(action)) {
			String scenario_job_page=null2str(request.getSession().getAttribute("scenario_job_page"));
			String current_page = null2str(request.getParameter("current_page"));
			
			logger.debug("[current_page:scenario_job_page] = [" + current_page+":"+scenario_job_page+"]");
			if(current_page.equals(scenario_job_page) || true){
				//前端查getsession之後 對的才叫截圖存檔
				//轉dataURL to png檔 return 檔名
				String dataURL = null2str(request.getParameter("dataURL"));
				String png_name = null2str(request.getParameter("png_name"));
				String file_name = png_name;//+".png";
				String file_locate = getServletConfig().getServletContext().getInitParameter("resultImg")+file_name;
				
				File parent_folder = new File(getServletConfig().getServletContext().getInitParameter("resultImg"));
				if(!parent_folder.exists()){
					parent_folder.mkdir();
				}
				
				JSONArray request_VO = new JSONArray();
				request_VO.put(CommonMethod.null2str(request.getParameter("png_name")));
				request_VO.put(CommonMethod.null2str(request.getParameter("dataURL")));
				
				new CommonMethod(getServletConfig())
						.useMysqlDBString("call sp_insert_image(?,?)",request_VO);
				logger.debug("get_dataURL: "+dataURL.substring(0, 30)+"...(neglect)");
				logger.debug("save_to: "+file_locate);
				response.setContentType("text/text");
				byte[] imagedata = DatatypeConverter.parseBase64Binary(dataURL.substring(dataURL.indexOf(",") + 1));
				BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(imagedata));
				ImageIO.write(bufferedImage, "png", new File(file_locate));
				response.getWriter().write(file_name);
			}else{
				logger.debug("[Output]: not_in_scenario_no_save_snap");
				response.getWriter().write("not_in_scenario");
			}
		}else if("save_URL_to_PNG".equals(action)) {
			
			String imageUrl = null2str(request.getParameter("imageUrl"));
			//String imageUrl="http://localhost:8080/sbi/record.do?action=get_image&png_name=9e0f052f-9d72-4513-883a-1ff4fdbfbf48.png";
			String png_name = null2str(request.getParameter("png_name"));
			
			if("failed".equals(png_name)){
				response.getWriter().write("error");
				return;
			}
			
			String file_name = png_name;//+".png";
			String file_locate = getServletConfig().getServletContext().getInitParameter("resultImg")+file_name;
			logger.debug("imageUrl: "+imageUrl);
			logger.debug("png_name: "+png_name);
			
			URL url = new URL(imageUrl);
			BufferedImage image = ImageIO.read(url);
			ImageIO.write(image, "png", new File(file_locate));
			
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, "png", baos);
			String dataUrl = "data:image/png;base64," +
				    DatatypeConverter.printBase64Binary(baos.toByteArray());
			
			JSONArray request_VO = new JSONArray();
			request_VO.put(png_name);
			request_VO.put(dataUrl);
			new CommonMethod(getServletConfig())
					.useMysqlDBString("call sp_insert_image(?,?)",request_VO);
			
			response.getWriter().write("success");
		}else if("get_image".equals(action)) {
			String png_name = null2str(request.getParameter("png_name"));
			String png_locate = null2str(getServletConfig().getServletContext().getInitParameter("resultImg")) + png_name;
			logger.debug("png_name: "+png_name);
			File f = new File(png_locate);
			
			try {
				if(png_name=="failed"){
					throw new NoSuchMethodException();
				}
				
				try{
					BufferedImage bi = ImageIO.read(f);
					response.setContentType("image/png");
					OutputStream out = response.getOutputStream();
					ImageIO.write(bi, "png", out);
					out.close();
					logger.debug("get_image_local: "+png_locate);
				} catch (Exception e) {
					logger.debug("full-range-log",e);
					JSONArray request_VO = new JSONArray();
					request_VO.put(png_name);
					List<Map<String,String>> respon = new CommonMethod(getServletConfig())
							.useMysqlDB("call sp_select_image(?)",request_VO);
					
					if(respon.size()==0){
						throw new NoSuchMethodException();
					}
					
					String imageUrl = respon.get(0).get("dataURL");
					BufferedImage bi = ImageIO.read(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(imageUrl.substring(imageUrl.indexOf(",") + 1))));
					response.setContentType("image/png");
					OutputStream out = response.getOutputStream();
					ImageIO.write(bi, "png", out);
					out.close();
					logger.debug("get_image_db: "+png_locate);
				}
//				BufferedImage bi = ImageIO.read(f);
			} catch (Exception e) {
				RequestDispatcher successView = request.getRequestDispatcher("./images/scenarioPicNotFound.png");
				successView.forward(request, response);
				logger.debug("output_png_notfound: "+e.toString());
				logger.debug("full-range-log",e);
			}
		}else if("output_record".equals(action)) {
			String job_id = null2str(request.getParameter("job_id"));
//			String group_id = null2str(request.getSession().getAttribute("group_id"));
			String dest = getServletConfig().getServletContext().getInitParameter("resultImg")+job_id+".pdf";
//			if(new File(dest).exists()){//先判斷目錄存不存在
//				加上檢查有沒有更新 如果沒更新直接return 有更新重trans_pdf
//				response.getWriter().write("success");
//				logger.debug("already there");
//				return;
//			}
			logger.debug("job_id: "+job_id);
			RecordService record = new RecordService();
			List<RecordVO> record_info = (List<RecordVO>) record.get_record_info(job_id, null);
			float left = 80, right = 80, top = 60, bottom = 60;
			
		    try{
		    	Document document = new Document(PageSize.A4, left, right, top, bottom);
		    	BaseFont bfChinese = BaseFont.createFont(this.getClass().getResource("")+"/kaiu.ttf", "Identity-H", BaseFont.NOT_EMBEDDED);
		    	Font titleFont = new Font(bfChinese, 48 ,Font.BOLD);
		    	Font title2Font = new Font(bfChinese, 28 ,Font.BOLD);
		    	Font titleExplanationFont = new Font(bfChinese, 18 ,Font.NORMAL);
		    	Font contentFont = new Font(bfChinese, 14 ,Font.NORMAL);
		    	Font blue = new Font(bfChinese, 22, Font.ITALIC, BaseColor.BLUE);
		    	
		    	PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(dest));
	            TableHeader event = new TableHeader();
	            writer.setPageEvent(event);
		        document.open();
				document.newPage();
				
	            //第一頁
//				Paragraph title_para = new Paragraph("\n\n"+null2str(record_info.get(0).getScenario_name()), titleFont);
//	            title_para.setAlignment(Element.ALIGN_CENTER);
//	            document.add(title_para);
//	            LineSeparator linesep = new LineSeparator();
//	            linesep.setLineWidth(3);
//	            
//				Paragraph line = new Paragraph("   ",titleExplanationFont);
//				line.add(new Chunk(linesep));
//				line.add("   ");
//				document.add(line);
//				document.add(new Paragraph("\n"+record_info.get(0).getScenario_explanation()+"\n\n\n\n\n\n\n", titleExplanationFont));
//				Anchor we = new Anchor("智能雲端市場定位系統",blue);
//				we.setReference("http://www.pershing.com.tw/");
//				Paragraph we_para = new Paragraph();
//				we_para.setAlignment(Element.ALIGN_CENTER);
//				we_para.add(we);
//				document.add(we_para);
//				
//				
//				//第二頁
//				document.newPage();
				String[] number_arr = {"零","一","二","三","四","五","六","七","八","九","十","","","","","","","","","",""};
				int[] page = {1,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
				int count_last_step=0;
				int count_png_out=0;
				List<ScenarioResultVO> count_result_page=record_info.get(0).getResults();
				int counter = 0 ;
				int j;
				for(j = 0;j < count_result_page.size(); j ++){
					if(count_last_step!=Integer.parseInt(count_result_page.get(j).getStep())||count_png_out==1){
						count_last_step = Integer.parseInt(count_result_page.get(j).getStep());
						counter++;
						count_png_out=0;
					}
					if(count_result_page.get(j).getPng_name().length() > 0 && new File(getServletConfig().getServletContext().getInitParameter("resultImg")+count_result_page.get(j).getPng_name()).exists()){
						count_png_out=1;
					}
					page[Integer.parseInt(count_result_page.get(j).getStep())+1]=counter+2;
				}
				page[Integer.parseInt(count_result_page.get(j-1).getStep())+1]=0;
//				
//				Paragraph title2_para = new Paragraph("目次\n\n", title2Font);
//				title2_para.setAlignment(Element.ALIGN_CENTER);
//				document.add(title2_para);
//				for(int i = 0;i < record_info.size(); i ++){
//			        Paragraph p2 = new Paragraph(number_arr[Integer.parseInt(record_info.get(i).getFlow_seq())]+"、　"+record_info.get(i).getFlow_name(),titleExplanationFont);
//			        DottedLineSeparator dottedline = new DottedLineSeparator();
//			        dottedline.setOffset(-2);
//			        p2.add(new Chunk(dottedline));  
//			        if(page[Integer.parseInt(record_info.get(i).getFlow_seq())]!=0){
//				        p2.add("P"+(page[Integer.parseInt(record_info.get(i).getFlow_seq())]));
//				    }else{
//				    	p2.add("從缺");
//				    }
//				    document.add(p2);
//					
//			    }
				
				//圖片頁
				int last_step=0;
				int png_out=0;
				List<ScenarioResultVO> results=record_info.get(0).getResults();
				String search_poi="";
				String search_BD="";
				String search_hot="";
				
				
				for(int i = 0;i < results.size(); i ++){
//					logger.debug(last_step+" len: "+Integer.parseInt(results.get(i).getStep()));
					if(last_step!=Integer.parseInt(results.get(i).getStep())){
						last_step = Integer.parseInt(results.get(i).getStep());
						document.newPage();
						
						Paragraph title_para = new Paragraph(null2str(record_info.get(0).getScenario_name()), title2Font);
			            title_para.setAlignment(Element.ALIGN_CENTER);
			            document.add(title_para);
			            
						String content = "第"+number_arr[Integer.parseInt(results.get(i).getStep())]+"步、 ["+record_info.get(Integer.parseInt(results.get(i).getStep())-1).getFlow_name()+"] :"+record_info.get(Integer.parseInt(results.get(i).getStep())-1).getFlow_explanation()+"\n\n";
						Paragraph content_para = new Paragraph(content, contentFont);
						document.add(content_para);
						png_out=0;
					}else if(png_out==1){
						document.newPage();
						png_out=0;
					}
					//有空改用results.get(index).getPage()去判斷頁面 不然修改scenario要重新加
					
					if("自定義之截圖".equals(results.get(i).getCategory())){
						String contentCR = "自定義之截圖 : ";
						Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
						document.add(contentCR_para);
					}else if("商圈資訊".equals(results.get(i).getFlow_name())){
						if("地圖資訊圖片".equals(results.get(i).getResult())){
							results.get(i).setResult("地圖資訊圖片↓");
						}
						String contentCR = results.get(i).getCategory() +" : "+results.get(i).getResult();
						Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
						document.add(contentCR_para);
					}else if("目標客群定位".equals(results.get(i).getFlow_name())){
						String contentCR = results.get(i).getCategory() +" : "+results.get(i).getResult();
						Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
						document.add(contentCR_para);
					}else if("動態統計-國家".equals(results.get(i).getFlow_name())){
						if("動態統計-國家".equals(results.get(i).getCategory())){
							results.get(i).setResult("選擇條件如下↓");
						}
						String contentCR = results.get(i).getCategory() +" : "+results.get(i).getResult();
						Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
						document.add(contentCR_para);
					}else if("動態統計-目標產業".equals(results.get(i).getFlow_name())){
						if("動態統計-目標產業".equals(results.get(i).getCategory())){
							results.get(i).setResult("選擇條件如下↓");
						}
						String contentCR = results.get(i).getCategory() +" : "+results.get(i).getResult();
						Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
						document.add(contentCR_para);
					}else if("電子書".equals(results.get(i).getFlow_name())){
						
						if("參閱電子書".equals(results.get(i).getCategory())){
							String contentCR = results.get(i).getCategory() +" : ";
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							
							Anchor pdf = new Anchor(results.get(i).getResult().split(": ")[0],blue);
							pdf.setReference(results.get(i).getResult().split(": ")[1]);
							contentCR_para.add(pdf);
							document.add(contentCR_para);
						}
					}else if("區位選擇".equals(results.get(i).getFlow_name())||"環域分析".equals(results.get(i).getFlow_name())||"區位選擇+環域分析".equals(results.get(i).getFlow_name())){
						if("區位選擇".equals(results.get(i).getCategory())){
							JSONObject jsonobj = new JSONObject("{\"result\":"+results.get(i).getResult()+"}");
							String ans = "評估出了 "+jsonobj.getJSONArray("result").getJSONArray(12).length()+" 個建議商圈: ";
							for(int k=0;k<jsonobj.getJSONArray("result").getJSONArray(12).length();k++){
								ans+=jsonobj.getJSONArray("result").getJSONArray(12).getJSONObject(k).get("City")+" ";
							}
							String contentCR = ans;
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							document.add(contentCR_para);
						}
						if("環域分析".equals(results.get(i).getCategory())){
							JSONObject jsonobj = new JSONObject("{\"result\":"+results.get(i).getResult()+"}");
							String ans = "分析了 "+(jsonobj.getJSONArray("result").length()-1)+" 個地理位置點";
							String contentCR = ans;
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							document.add(contentCR_para);
						}
						if("完成此步驟".equals(results.get(i).getCategory())){
							if("地圖資訊圖片".equals(results.get(i).getResult())){
								results.get(i).setResult("分析如下↓");
							}
							String contentCR = results.get(i).getCategory() +" : "+results.get(i).getResult();
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							document.add(contentCR_para);
						}
					}else if("商圈 P O I".equals(results.get(i).getFlow_name())||"商圈 POI".equals(results.get(i).getFlow_name())||"環域分析+商圈POI".equals(results.get(i).getFlow_name())||"輸入地址".equals(results.get(i).getFlow_name())){
						if("查詢地址".equals(results.get(i).getCategory())){
							JSONObject jsonobj = new JSONObject("{\"result\":"+results.get(i).getResult()+"}");
							String ans = "查詢了 "+jsonobj.getJSONArray("result").length()+" 個地址: ";
							for(int k=0;k<jsonobj.getJSONArray("result").length();k++){
								ans+=jsonobj.getJSONArray("result").get(k)+" ";
							}
							String contentCR = ans;
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							document.add(contentCR_para);
						}else if("查詢商圈".equals(results.get(i).getCategory())){
							String contentCR = results.get(i).getResult();
							search_BD+= (search_BD.length()==0?"":", ")+ contentCR;
//							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
//							document.add(contentCR_para);
						}else if("查詢POI".equals(results.get(i).getCategory())){
							JSONObject jsonobj = new JSONObject("{\"result\":"+results.get(i).getResult()+"}");
							//String contentCR = "查詢了POI: "+jsonobj.getJSONArray("result").get(3)+"";
							search_poi+= (search_poi.length()==0?"":", ")+ jsonobj.getJSONArray("result").get(3);
//							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
//							document.add(contentCR_para);
						}else if("查詢熱力圖".equals(results.get(i).getCategory())){
							JSONObject jsonobj = new JSONObject("{\"result\":"+results.get(i).getResult()+"}");
							//String contentCR = "查詢了類別 '"+jsonobj.getJSONArray("result").get(3)+"' 的熱力圖";
							search_hot+= (search_hot.length()==0?"":", ")+ jsonobj.getJSONArray("result").get(3);
//							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
//							document.add(contentCR_para);
						}else if("環域分析".equals(results.get(i).getCategory())){
							JSONObject jsonobj = new JSONObject("{\"result\":"+results.get(i).getResult()+"}");
							String ans = "分析了 "+(jsonobj.getJSONArray("result").length()-1)+" 個地理位置點";
							String contentCR = ans;
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							document.add(contentCR_para);
						}else if("完成此步驟".equals(results.get(i).getCategory())){
							PdfPTable table = new PdfPTable(2);
							table.setWidthPercentage(100);
							table.setWidths(new float[] { 1, 3 });
							table.getDefaultCell().setBorder(0);
							
							if(search_poi.length()!=0){
//								String contentCR = "查詢了POI: "+search_poi+"。";
//								Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
//								document.add(contentCR_para);
								Paragraph contentCR_para = new Paragraph("   查詢了POI: ", contentFont);
								Paragraph search_poi_para = new Paragraph(search_poi, contentFont);
								contentCR_para.setAlignment(Element.ALIGN_RIGHT);
								table.addCell(contentCR_para);
								table.addCell(search_poi_para);
								table.addCell(" ");
								table.addCell(" ");
								search_poi="";
							}
							if(search_BD.length()!=0){
//								String contentCR = "查詢了商圈: "+search_BD+"。";
//								Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
//								document.add(contentCR_para);
								Paragraph contentCR_para = new Paragraph("  查詢了商圈: ", contentFont);
								Paragraph search_BD_para = new Paragraph(search_BD, contentFont);
								contentCR_para.setAlignment(Element.ALIGN_RIGHT);
								table.addCell(contentCR_para);
								table.addCell(search_BD_para);
								table.addCell(" ");
								table.addCell(" ");
								search_BD="";
							}
							if(search_hot.length()!=0){
//								String contentCR = "查詢了熱力圖類別: "+search_hot+"。";
//								Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
//								document.add(contentCR_para);
								Paragraph contentCR_para = new Paragraph("查詢了熱力圖: ", contentFont);
								Paragraph search_hot_para = new Paragraph(search_hot, contentFont);
								contentCR_para.setAlignment(Element.ALIGN_RIGHT);
								table.addCell(contentCR_para);
								table.addCell(search_hot_para);
								table.addCell(" ");
								table.addCell(" ");
								search_hot="";
							}
							
							document.add(table);
							
							if("地圖資訊圖片".equals(results.get(i).getResult())){
								results.get(i).setResult("地圖資訊圖片↓");
							}
							//String contentCR = results.get(i).getCategory() +" : "+results.get(i).getResult();
							String contentCR = results.get(i).getResult();
							Paragraph contentCR_para = new Paragraph(contentCR, contentFont);
							document.add(contentCR_para);
						}
					}
					if(results.get(i).getPng_name().length()>0){ 
							//&& new File(getServletConfig().getServletContext().getInitParameter("resultImg")+results.get(i).getPng_name()).exists()){
						document.add( new Paragraph("\n",contentFont));
						
						//=============================
						JSONArray request_VO = new JSONArray();
						request_VO.put(results.get(i).getPng_name());
						List<Map<String,String>> respon = new CommonMethod(getServletConfig())
								.useMysqlDB("call sp_select_image(?)",request_VO);
						if(respon.size()!=0){
							String imageUrl = respon.get(0).get("dataURL");
							BufferedImage bi = ImageIO.read(new ByteArrayInputStream(DatatypeConverter.parseBase64Binary(imageUrl.substring(imageUrl.indexOf(",") + 1))));
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ImageIO.write(bi, "png", baos);
							Image img = Image.getInstance(baos.toByteArray());
							//=============================
	//						Image img = Image.getInstance(getServletConfig().getServletContext().getInitParameter("resultImg")+results.get(i).getPng_name());
							img.scaleToFit(440,400);
							document.add(img); 
							
							png_out=1;
						}
						
						
					}
				}
				document.close();
				
				response.getWriter().write("success");
		        logger.debug("trans_success");
		    }catch(Exception e){
		    	e.printStackTrace(System.err);
		    	
				logger.debug("trans_pdf_error");
				response.getWriter().write("trans_pdf_error");
				logger.debug("full-range-log",e);
			}
		    
		}
	}
	
	public class RecordService {
		private record_interface dao;

		public RecordService() {
			dao = new RecordDAO();
		}
		public List<ScenarioResultVO> get_job_info(String group_id,String job_id) {
			return dao.get_job_info(group_id,job_id);
		}
//		List<RecordVO>
		public Object get_record_info(String job_id,String ret) {
			return dao.get_record_info(job_id,ret);
		}
	}

	/*************************** 制定規章方法 ****************************************/
	interface record_interface {
		public List<ScenarioResultVO> get_job_info(String group_id,String job_id);
		public Object get_record_info(String job_id,String ret);
		
	}
	
	/*************************** 操作資料庫 ****************************************/
	class RecordDAO implements record_interface{
		private final String dbURL = getServletConfig().getServletContext().getInitParameter("dbURL")
				+ "?useUnicode=true&characterEncoding=utf-8&useSSL=false";
		private final String dbUserName = getServletConfig().getServletContext().getInitParameter("dbUserName");
		private final String dbPassword = getServletConfig().getServletContext().getInitParameter("dbPassword");
		
		private static final String sp_select_scenario_job_info = "call sp_select_scenario_job_info(?)";
		private static final String sp_scenario_get_record_info  = "call sp_scenario_get_record_info (?)" ;
		@Override
		public List<ScenarioResultVO> get_job_info(String group_id,String job_id){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			String old_result = "[]";
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_select_scenario_job_info);
				
				pstmt.setString(1, group_id);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					if(job_id.equals(null2str(rs.getString("job_id")))){
						old_result=null2str(rs.getString("result"));
					}
				}
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
			List<ScenarioResultVO> old_json_result = new Gson().fromJson(old_result, new TypeToken<List<ScenarioResultVO>>() {}.getType());
			return old_json_result;
		}
		public Object get_record_info(String job_id,String ret){
			Connection con = null;
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			RecordVO recordvo = null;
			List<RecordVO> list =new ArrayList<RecordVO>();
			try {
				Class.forName("com.mysql.jdbc.Driver");
				con = DriverManager.getConnection(dbURL, dbUserName, dbPassword);
				pstmt = con.prepareStatement(sp_scenario_get_record_info);
				
				pstmt.setString(1, job_id);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					recordvo = new RecordVO();
					recordvo.setScenario_name(null2str(rs.getString("_scenario_name")));
					recordvo.setScenario_explanation(null2str(rs.getString("_scenario_explanation")));
					recordvo.setFlow_seq(null2str(rs.getString("flow_seq")));
					recordvo.setFlow_name(null2str(rs.getString("flow_name")));
					recordvo.setFlow_explanation(null2str(rs.getString("explanation")));
					recordvo.setResults(new Gson().fromJson(null2str(rs.getString("_result")), new TypeToken<List<ScenarioResultVO>>() {}.getType()));
					list.add(recordvo);
					
				}
			} catch (SQLException se) {
				// Handle any driver errors
				throw new RuntimeException("A database error occured. " + se.getMessage());
			} catch (ClassNotFoundException cnfe) {
				throw new RuntimeException("A database error occured. " + cnfe.getMessage());
			} finally {
				// Clean up JDBC resources
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (pstmt != null) {
					try {
						pstmt.close();
					} catch (SQLException se) {
						se.printStackTrace(System.err);
						logger.debug("full-range-log",se);
					}
				}
				if (con != null) {
					try {
						con.close();
					} catch (Exception e) {
						e.printStackTrace(System.err);
						logger.debug("full-range-log",e);
					}
				}
			}
			return list;
		}
	}
	
	public String null2str(Object object) {
		return object == null ? "" : object.toString();
	}
}
class TableHeader extends PdfPageEventHelper {
    /** The header text. */
    String header;
    /** The template with the total number of pages. */
    PdfTemplate total;

    /**
     * Allows us to change the content of the header.
     * @param header The new header String
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * Creates the PdfTemplate that will hold the total number of pages.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
        total = writer.getDirectContent().createTemplate(30, 16);
    }

    /**
     * Adds a header to every page
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
        PdfPTable table = new PdfPTable(1);
        try {
            table.setWidths(new int[]{1});
            table.setTotalWidth(20);
            table.getDefaultCell().setBorder(PdfPCell.NO_BORDER);
            //table.addCell((writer.getPageNumber()==1?"":String.format("%d", writer.getPageNumber()-1)));
            table.writeSelectedRows(0, -1, 291, document.bottom()+table.getTotalHeight()-20,writer.getDirectContent());
        }
        catch(DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }
}
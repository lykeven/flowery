package servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import demo.opencvtest;

public class Uploadfile extends HttpServlet {
	
	private String Path="./data//testpictures//";
	/**
	 * Constructor of the object.
	 */
	public Uploadfile() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("start uploading....");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		//��ô����ļ���Ŀ������
		DiskFileItemFactory factory = new DiskFileItemFactory();
		//��ȡ�ļ��ϴ���Ҫ�����·����upload�ļ�������ڡ�
		//String path = request.getSession().getServletContext().getRealPath("/testpictures");
		//������ʱ����ļ��Ĵ洢�ң�����洢�ҿ��Ժ����մ洢�ļ����ļ��в�ͬ����Ϊ���ļ��ܴ�Ļ���ռ�ù����ڴ��������ô洢�ҡ�
		factory.setRepository(new File(Path));
		//���û���Ĵ�С�����ϴ��ļ���������������ʱ���ͷŵ���ʱ�洢�ҡ�
		factory.setSizeThreshold(4000*5000);
		//�ϴ��������ࣨ��ˮƽAPI�ϴ�������
		ServletFileUpload upload = new ServletFileUpload(factory);
		
		try{
			//���� parseRequest��request������  ����ϴ��ļ� FileItem �ļ���list ��ʵ�ֶ��ļ��ϴ���
			@SuppressWarnings("unchecked")
			List<FileItem> list = (List<FileItem>)upload.parseRequest(request);
			for(FileItem item:list){
				//��ȡ���������֡�
				String name = item.getFieldName();
				//�����ȡ�ı���Ϣ����ͨ���ı���Ϣ����ͨ��ҳ�����ʽ���������ַ�����
				if(item.isFormField()){
					//��ȡ�û�����������ַ�����
					String value = item.getString();
					request.setAttribute(name, value);
				}
				//���������ǷǼ��ַ���������ͼƬ����Ƶ����Ƶ�ȶ������ļ���
				else{ 
					//��ȡ·����
					String value = item.getName();
					//ȡ�����һ����б�ܡ�
					int start = value.lastIndexOf("\\");
					//��ȡ�ϴ��ļ��� �ַ������֡�+1��ȥ����б�ܡ�
					String filename = value.substring(start+1);
					request.setAttribute(name, filename);
					
					/*�������ṩ�ķ���ֱ��д���ļ��С�
					 * item.write(new File(path,filename));*/
					//�յ�д�����յ��ļ��С�
					OutputStream out = new FileOutputStream(new File(Path,filename));
					InputStream in = item.getInputStream();
					
					int length = 0;
					byte[] buf = new byte[1024];
					System.out.println("��ȡ�ļ�����������:"+ item.getSize());
					
					while((length = in.read(buf))!=-1){
						out.write(buf,0,length);
					}
					in.close();
					out.close();
					
					opencvtest t=new opencvtest(Path+filename,1,2);
					String s=t.getMaxscoreFlower();
					String ss=t.getretstr();
					String rs=s+"&"+ss;
					System.out.println(rs);
					response.getWriter().write(rs);
					response.flushBuffer();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}

package com.project.identification;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;


public class HttpAssist {
	private static final String TAG = "uploadFile";
	private static final int TIME_OUT = 10 * 1000000; // ��ʱʱ��
	private static final String CHARSET = "utf-8"; // ���ñ���
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";

	public  String uploadFile(File file) {
		String BOUNDARY = UUID.randomUUID().toString(); // �߽��ʶ �������
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // ��������
		String RequestURL = "http://identificationtest.nat123.net/test/servlet/Uploadfile";
		String Result=null;
		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // ����������
			conn.setDoOutput(true); // ���������
			conn.setUseCaches(false); // ������ʹ�û���
			conn.setRequestMethod("POST"); // ����ʽ
			conn.setRequestProperty("Charset", CHARSET); // ���ñ���
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary="+ BOUNDARY);
			if (file != null) {
				/**
				 * ���ļ���Ϊ�գ����ļ���װ�����ϴ�
				 */	
				OutputStream outputSteam = conn.getOutputStream();

				DataOutputStream dos = new DataOutputStream(outputSteam);
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * �����ص�ע�⣺ name�����ֵΪ����������Ҫkey ֻ�����key �ſ��Եõ���Ӧ���ļ�
				 * filename���ļ������֣�������׺���� ����:abc.png
				 */

				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""
						+ file.getName() + "\"" + LINE_END);
				//sb.append("Content-Type: application/octet-stream; charset="+ CHARSET + LINE_END);
				sb.append("Content-Type: image/jpeg; charset="+ CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				/*
				InputStream is = new FileInputStream(file);
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				*/
				String path=file.getPath();
				String name=file.getName();
				PictureUtil pc=new PictureUtil();
				byte[] bytes=pc.bitmapTobytearray(path);
				int len=bytes.length;
				dos.write(bytes, 0, len);
				
				
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END)
						.getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * ��ȡ��Ӧ�� 200=�ɹ� ����Ӧ�ɹ�����ȡ��Ӧ����
				 */
				int res = conn.getResponseCode();
				InputStream in =conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br = new BufferedReader(isr);
				 Result=br.readLine();
				
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Result;
	}


}
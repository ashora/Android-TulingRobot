package com.echo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;
import java.util.Date;

import com.echo.bean.ChatMessage;
import com.echo.bean.ChatMessage.Type;
import com.echo.bean.Result;
import com.google.gson.Gson;


public class HttpUtils {
	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String APIKEY = "1e72fa065762a6437081c7d29e506732";
	
	/**
	 * 发送一个消息，得到返回的消息
	 * @param msg
	 * @return chatMessage
	 */
	public static ChatMessage sendMessage(String msg){
		ChatMessage chatMessage = new ChatMessage();
		String jsonRes = doGet(msg);
		Gson gson = new Gson();
		Result result = null;
		try{
		result = gson.fromJson(jsonRes, Result.class);
		chatMessage.setMsg(result.getText());
		}catch(Exception e)
		{
			chatMessage.setMsg("服务器繁忙，请稍候再试");
		}
		chatMessage.setDate(new Date());
		chatMessage.setType(Type.INCOMING);
		return chatMessage;
	}
	
	public static String doGet(String msg){
		String result = "";
		String url =  setParams(msg);
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		
	    //将流转换为一个String
		try {		
			java.net.URL getUrl = new java.net.URL(url); 
			HttpURLConnection conn = (HttpURLConnection) getUrl.openConnection(); 
			//设定HttpURLConnection参数5秒，请求方式为Get
			conn.setReadTimeout(5 * 1000);
			conn.setConnectTimeout(5 * 1000);	
			conn.setRequestMethod("GET");    
			is = conn.getInputStream();
			//将流转换为一个String
			int len = -1;
			byte[] buf = new byte[128];
			baos = new ByteArrayOutputStream();

			while ((len = is.read(buf)) != -1)
			{
				baos.write(buf, 0, len);
			}
			baos.flush();
			result = new String(baos.toByteArray());
			
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try
			{
				if (baos != null)
					baos.close();
			} catch (IOException e)
			{
				e.printStackTrace();
			}

			try
			{
				if (is != null)
				{
					is.close();
				}
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}		
		return result;		
	}

	private static String setParams(String msg) {
		String url = "";
		try {
			url =  URL + "?key=" + APIKEY + "&info="
						+ URLEncoder.encode(msg, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return url;
	}

}

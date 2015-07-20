# Android-TulingRobot
学习图灵机器人API的使用以及ListView的使用
**之前在[慕课网][1]学习了有关[图灵机器人][2]的使用，今天再回顾一下图灵机器人API的使用以及ListView多种Item布局时的处理进行巩固学习**
[1]:http://www.imooc.com/
[2]:http://www.tuling123.com/openapi/
###主要内容
#### 第三方API(图灵机器人)的使用
#### ListView多种Item布局时的处理

**图灵机器人介绍**
图灵机器人平台,基于自然语言处理、知识库和云计算等技术,为广大开发者、合作伙伴提供的一系列智能语义处理能力(包括语义理解、智能问答、知识库对接等)的服务平台。详情可点击[图灵机器人官网][2]查看。

#### 编写一个工具类实现消息的发送和接收
``` java
package com.echo.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URLEncoder;

public class HttpUtils {
	private static final String URL = "http://www.tuling123.com/openapi/api";
	private static final String APIKEY = "1e72fa065762a6437081c7d29e506732";
	
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

```
#### 搭建测试环境，对工具类进行测试
**1、搭建测试环境：在AndroidManifest.xml文件中添加如下命令**
a、在application中添加
```
<uses-library android:name="android.test.runner"/>
```
b、在application外添加instrumentation标签        

	<instrumentation
        android:name="android.test.InstrumentationTestRunner"
        android:label="this is a test "
        android:targetPackage="com.echo.tuling" >
    </instrumentation> 
**2、编写测试类**
``` java
package com.echo.test;

import com.echo.utils.HttpUtils;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestHttpUtils extends AndroidTestCase{	
	public void testSendInfo(){
	String res = HttpUtils.doGet("Hi，你好。");
	Log.d("TAG", res);
	}
}
```   
#### 完成消息实体的编写
编写消息对话的Bean,消息名称、消息内容、时间等。
``` java
package com.echo.bean;

import java.util.Date;

public class ChatMessage {
	private String name;
	private String msg;
	private Type type;
	private Date date;	

	public enum Type
	{
		INCOMING, OUTCOMING
	}

	public ChatMessage(String msg, Type type, Date date)
	{
		super();
		this.msg = msg;
		this.type = type;
		this.date = date;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
```
完善工具类
在之前的工具类HttpUtils.java类中，实现了发送Get请求，并且返回Get请求的结果。现在需要实现用户发送一个消息，服务器返回一个ChatMessage对象作为返回结果。首先分析一下返回数据的格式如下，它是一个Json格式：
``` {"code":100000 ,"text":"********"} ```

编写一个Result.java映服务器返回的结果
``` java
package com.echo.bean;

public class Result {
	private int code;
	private String text;
	
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
```
完善编写工具类，添加sendMessage方法，功能是发送一个消息，得到返回的消息
```java
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
```
#### 完成ListView布局

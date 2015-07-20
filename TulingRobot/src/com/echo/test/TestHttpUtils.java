package com.echo.test;

import com.echo.utils.HttpUtils;

import android.test.AndroidTestCase;
import android.util.Log;

public class TestHttpUtils extends AndroidTestCase{
	
	public void testSendInfo(){
	String res = HttpUtils.doGet("Hi£¬ÄãºÃ¡£");
	Log.d("TAG", res);
	}

}

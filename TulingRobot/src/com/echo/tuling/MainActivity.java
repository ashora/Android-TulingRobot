package com.echo.tuling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.echo.bean.ChatMessage;
import com.echo.bean.ChatMessage.Type;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.app.Activity;

public class MainActivity extends Activity {

	//定义ListView展示所有的消息，以及ListView需要的适配器,适配器需要数据源，List中存放ChatMessage
	//ListView绑定一个适配器、适配器绑定一个数据源，成对出现。
	private ListView mMsgs;
	private ChatMessageAdapter mAdapter;
	private List<ChatMessage> mDatas;
	
	private EditText mInputMsg;
	private Button mSendMsg;
	
	private Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg)
		{
			// 等待接收，子线程完成数据的返回
			ChatMessage fromMessge = (ChatMessage) msg.obj;
			mDatas.add(fromMessge);
			mAdapter.notifyDataSetChanged();
			mMsgs.setSelection(mDatas.size()-1);
		};

	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		
		//变量初始化
		initView();
		initDatas();
		// 初始化事件
		initListener();
	}

	private void initListener() {
		// TODO Auto-generated method stub
		mSendMsg.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				final String toMsg = mInputMsg.getText().toString();
				if (TextUtils.isEmpty(toMsg))
				{
					Toast.makeText(MainActivity.this, "发送消息不能为空！",
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				ChatMessage toMessage = new ChatMessage();
				toMessage.setDate(new Date());
				toMessage.setMsg(toMsg);
				toMessage.setType(Type.OUTCOMING);
				mDatas.add(toMessage);
				mAdapter.notifyDataSetChanged();
				mMsgs.setSelection(mDatas.size()-1);
				
				mInputMsg.setText("");
				
				new Thread()
				{
					public void run()
					{
						ChatMessage fromMessage = com.echo.utils.HttpUtils.sendMessage(toMsg);
						Message m = Message.obtain();
						m.obj = fromMessage;
						mHandler.sendMessage(m);
					};
				}.start();
			}
		});
	}

	private void initDatas() {
		// TODO Auto-generated method stub
		mDatas = new ArrayList<ChatMessage>();
		mDatas.add(new ChatMessage("你好，图灵机器人为您服务", Type.INCOMING, new Date()));
		mAdapter = new ChatMessageAdapter(this, mDatas);
		mMsgs.setAdapter(mAdapter);
		
	}

	private void initView() {
		// TODO Auto-generated method stub
		mMsgs = (ListView) findViewById(R.id.id_listview_msgs);
		mInputMsg = (EditText) findViewById(R.id.id_input_msg);
		mSendMsg = (Button) findViewById(R.id.id_send_msg);
	}

}

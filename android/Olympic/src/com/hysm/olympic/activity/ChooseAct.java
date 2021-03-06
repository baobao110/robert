package com.hysm.olympic.activity;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hysm.olympic.R;
import com.hysm.olympic.bean.PublicData;
import com.hysm.olympic.tool.Date_tool; 
import com.hysm.olympic.tool.MyToast;
import com.hysm.olympic.tool.Net_tool;
import com.hysm.olympic.tool.Speek; 
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult; 
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent; 
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView; 

public class ChooseAct extends Activity {

	private TextView name_text,studentid_text;
	private TextView ask_text,guess_text,pk_text;
	private ImageView bookimg,lximg,pkimg,musicimg;
	
	private ImageView exit_img;
	
	
	// 语音识别对象
    private SpeechRecognizer recognizer; 
    //语音合成对象
    private SpeechSynthesizer speaker; 
    //识别出来的句子
    private StringBuilder sentence = new StringBuilder() ; 
    //handler
 	private Handler mHandler;
 	
 	// 识别成功
 	public static final int SPEECH_SUCCESS = 0;
 	// 识别失败
 	public static final int SPEECH_FAIL = -1;
 	// 开始识别
 	public static final int SPEECH_START = 1;
 	// 识别出错
 	public static final int SPEECH_ERROR = 2; 
    // 识别结束
  	public static final int SPEECH_END = 3; 
    // 识别进度
   	public static final int SPEECH_Progress = 4;
  	
  	//语言开始播放
  	public static final int SAY_START =10;
  	//语言播放结束
  	public static final int SAY_END =11;
  	 
  	private String student_name="",student_id="";
  	
  	private SharedPreferences preferences;
  	
  	private long old_time = 0; 
  	
  	private int img_show = 0;
  	
  	private String say_info="";
  	
   
	
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		BaseApplication.getInstance().addActivity(this); // 添加Activity到堆栈
		setContentView(R.layout.choose);
		  
	}
  

	private void init_view() {
		name_text = (TextView)findViewById(R.id.choose_name_text);
		studentid_text = (TextView)findViewById(R.id.choose_studentid_text);
		
		bookimg = (ImageView)findViewById(R.id.choose_book_img);
		pkimg = (ImageView)findViewById(R.id.choose_pk_img);
		lximg = (ImageView)findViewById(R.id.choose_lx_img);
		musicimg = (ImageView)findViewById(R.id.choose_music_img);
		
		exit_img = (ImageView)findViewById(R.id.choose_exit_img);
		
		ask_text = (TextView)findViewById(R.id.choose_ask_text);
		guess_text = (TextView)findViewById(R.id.choose_guess_text);
		pk_text = (TextView)findViewById(R.id.choose_pk_text);
		img_show = 0;
		 
		
		Net_tool.checkNet(ChooseAct.this);
	}

	private void init_listener() {
		bookimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				PublicData.Catalog_Speak_state = 1;
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, CatalogAct.class);
				startActivity(intent);
				 
			}
		});
		
		lximg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, GuessAct.class);
				intent.putExtra("type", "2"); 
				startActivity(intent);
				 
			}
		});
		
		pkimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, GameAct.class); 
				startActivity(intent); 
				
			}
		});
		
		musicimg.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) { 
				if(recognizer.isListening()){
					recognizer.stopListening(); 
				}
				recognizer.destroy();
				if(speaker.isSpeaking()){
					speaker.stopSpeaking(); 
				}
				speaker.destroy();
				Intent intent = new Intent();
				intent.setClass(ChooseAct.this, MusicListAct.class); 
				startActivity(intent); 
				
			}
		});
		
		exit_img.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0){
				
				if(recognizer.isListening()){
					recognizer.stopListening();
				}
				if(speaker.isSpeaking()){
					speaker.stopSpeaking();
				}
				recognizer.destroy();
				speaker.destroy();
				Intent intent = new Intent(ChooseAct.this, MainActivity.class);
				startActivity(intent);
			}
		});
	}
	
	
	
	@Override
	protected void onResume(){ 
		super.onResume();
		
		old_time = 0;
		
		init_view(); 
		init_listener(); 
		init_crate(); 
		init_handler();  
		
		init_data();
		
		if(PublicData.Choose_Speak_state == 1){
			say_info = "您可以选择奥运知识学习，奥运知识竞答，也可以选择奥运音乐欣赏。"; 
			speaker.startSpeaking(say_info,synthesizerListener); 
			PublicData.Choose_Speak_state = 0;
		}else{
			
			//直接进入语音识别
			
			recognizer.startListening(recognizerListener);
			
		}
		
		
	}
	
	private void init_data() {
		preferences = getSharedPreferences(PublicData.RobotShare, Context.MODE_PRIVATE);
		student_id = preferences.getString("studentid", "");
		student_name = preferences.getString("studentname", "");
		
		name_text.setText(student_name);
		studentid_text.setText(student_id);
		
		if(img_show == 0){
			img_show = 1;
			bookimg.setVisibility(View.VISIBLE);
			ask_text.setVisibility(View.VISIBLE);
			
			lximg.setVisibility(View.GONE);
			guess_text.setVisibility(View.GONE);
			
			pkimg.setVisibility(View.VISIBLE);
			pk_text.setVisibility(View.VISIBLE);
		}
	}


	/**
	 * 初始化语音识别/语音合成
	 */
	private void init_crate() {
		
		Speek speek = new Speek(ChooseAct.this);
		 
		//初始化语音对象
        recognizer = speek.getRecognizer();
        speaker = speek.getSpeaker();
		 
	}
	
	private void init_handler() {
		 
		mHandler = new Handler() { 
			@Override
			public void handleMessage(Message msg){ 
				super.handleMessage(msg); 
				int what = msg.what;
				
				switch (what) {
				case SPEECH_SUCCESS:// 识别成功 
					//重置时间
					old_time = Date_tool.get_time(); 
					
					String result = (String) msg.obj;  
					word_check(result);
					break; 
				case SPEECH_FAIL:// 识别失败
					recognizer.startListening(recognizerListener); 
					break; 
				case SPEECH_START:
					 
					break;
				case SPEECH_END:
					 
					break;
				case SPEECH_ERROR: 
					String error = (String) msg.obj; 
					
					if(old_time == 0){
						//重置时间
						old_time = Date_tool.get_time();
						recognizer.startListening(recognizerListener); 
					}else{
						
						//检验时间是否超过2分钟，进入广告
						if(Date_tool.check_time(old_time)){ 
							if(recognizer.isListening()){
								recognizer.stopListening(); 
							}
							recognizer.destroy();
							if(speaker.isSpeaking()){
								speaker.stopSpeaking(); 
							}
							speaker.destroy();
							
							Intent intent = new Intent();
							intent.setClass(ChooseAct.this, AdvertAct.class);
							startActivity(intent); 
						}else{
							recognizer.startListening(recognizerListener); 
						}
					}
					  
					break;
				case SPEECH_Progress: 
					 
					break;
				case SAY_END:
					
					 
					//重置时间
					old_time = Date_tool.get_time();
					 
					//开始录音 
					recognizer.startListening(recognizerListener);
					
					break;
				case SAY_START:
					
					if(say_info!= null){
						 new MyToast(ChooseAct.this, say_info);
					}
					 
					break;
				}
			}
 
        	
        };
	}

	 
	//听写监听器
    private RecognizerListener recognizerListener = new RecognizerListener(){

    	/**
    	* 开始录音
    	*/
		@Override
		public void onBeginOfSpeech() {
			Message msg=mHandler.obtainMessage();
			msg.what=SPEECH_START;
			mHandler.sendMessage(msg);
		}

		 /**
		* 结束录音
		*/
		@Override
		public void onEndOfSpeech() {
			Message msg=mHandler.obtainMessage();
			msg.what=SPEECH_END;
			mHandler.sendMessage(msg);
		}

		/**
		* 会话发生错误回调接口
		* @param speechError
		*/
		@Override
		public void onError(SpeechError error) {
			// 回话发生错误时回调的函数
			String merror = error.getPlainDescription(true);// 获取错误码描述
			Message msg=mHandler.obtainMessage();
			msg.what=SPEECH_ERROR;
			msg.obj=merror;
			mHandler.sendMessage(msg);
		}

		/**
		* 扩展用接口
		*/
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			 
		}

		 /**
		* 听写结果回调接口 , 返回Json格式结果
		* @param recognizerResult  json结果对象
		* @param b                 等于true时会话结束
		*/
		@Override
		public void onResult(RecognizerResult recognizerResult, boolean isLast) {
			// 一般情况下会通过onResults接口多次返回结果，完整的识别内容是多次结果的累加；
			// 关于解析Json的代码可参见MscDemo中JsonParser类；
			// isLast等于true时会话结束。
			/***
			 * 结果--{"sn":1,"ls":false,"bg":0,"ed":0,"ws":[{"bg":0,"cw":[
			 * {"w":"两百","sc":0.00}]}]}
			 * 结果--{"sn":2,"ls":true,"bg":0,"ed":0,"ws"
			 * :[{"bg":0,"cw":[{"w":"。","sc":0.00}]}]}
			 */
			try{
				//多次获取结果，读写语音内容
				JSONObject mRsult = new JSONObject(recognizerResult.getResultString()); 
				JSONArray data = mRsult.getJSONArray("ws");
				for (int i = 0; i < data.length(); i++) {
					JSONObject w = data.getJSONObject(i);
					JSONArray array = w.getJSONArray("cw");
					for (int k = 0; k < array.length(); k++) {
						JSONObject cwdata = array.getJSONObject(k);
						sentence.append(cwdata.getString("w"));
					}
				}
				
				if (!mRsult.getBoolean("ls")) {
					 
					String rusult = sentence.toString();
					Message msgs = mHandler.obtainMessage();
					msgs.what = SPEECH_Progress;
					msgs.obj = rusult;
					mHandler.sendMessage(msgs);
					
				}else{
					//停止监听
					recognizer.stopListening(); 
					//发布发布监听内容 
					String rusult = sentence.toString(); 
					//语音读写区重置
					sentence= new StringBuilder();
					
					if(rusult!= null && !rusult.equals("")){
						Message msgs = mHandler.obtainMessage();
						msgs.what = SPEECH_SUCCESS;
						msgs.obj = rusult;
						mHandler.sendMessage(msgs);
					}else{
						Message msgs = mHandler.obtainMessage();
						msgs.what = SPEECH_FAIL;
						msgs.obj = rusult;
						mHandler.sendMessage(msgs);
					}
					 
				}
				
			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onVolumeChanged(int arg0) {
			// TODO Auto-generated method stub
			
		}

		/*@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			
		}*/

  
    };
    
    
  //语音合成监听器
    private SynthesizerListener synthesizerListener = new SynthesizerListener() {

    	 /**
         * 缓冲进度回调
         */
		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {
			 
		}
		/**
         * 会话结束回调接口，没有错误时，error为null
         */
		@Override
		public void onCompleted(SpeechError arg0) {
			Message msg=mHandler.obtainMessage();
			msg.what=SAY_END;
			mHandler.sendMessage(msg);
		}

		 /**
	     * 会话事件回调接口
	     */
		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
			 
		}

		/**
         * 开始播放
         */
		@Override
		public void onSpeakBegin() {
			Message msg=mHandler.obtainMessage();
			msg.what=SAY_START;
			mHandler.sendMessage(msg);
		}
		/**
         * 暂停播放
         */
		@Override
		public void onSpeakPaused() {
			 
		}

		 /**
         * 播放进度回调
         */
		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {
			 
		}
		/**
         * 恢复播放回调接口
         */
		@Override
		public void onSpeakResumed() {
			 
		}
    	
    };
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//点击按键事件 
		if(keyCode == KeyEvent.KEYCODE_BACK){ 
			if(recognizer.isListening()){
				recognizer.stopListening();
			}
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			recognizer.destroy();
			speaker.destroy(); 
			Intent intent = new Intent(ChooseAct.this, MainActivity.class);
			startActivity(intent);
		}
		return super.onKeyDown(keyCode, event);
	}

  
    
    protected void word_check(String str) {
		 
    	if(str.contains("退出")){
    		if(recognizer.isListening()){
				recognizer.stopListening();
			}
			if(speaker.isSpeaking()){
				speaker.stopSpeaking();
			}
			recognizer.destroy();
			speaker.destroy(); 
			BaseApplication.getInstance().exit();
		}else if(str.contains("学习") ){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			
			PublicData.Catalog_Speak_state = 1;
			
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, CatalogAct.class);
			startActivity(intent);
		}/*else if(str.contains("练习")){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, GuessAct.class);
			intent.putExtra("type", "2"); 
			startActivity(intent);
		}*/else if(str.contains("pk") || str.contains("竞答")){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, GameAct.class); 
			startActivity(intent);
		}else if(str.contains("音乐") || str.contains("欣赏")){
			if(recognizer.isListening()){
				recognizer.stopListening(); 
			}
			recognizer.destroy();
			if(speaker.isSpeaking()){
				speaker.stopSpeaking(); 
			}
			speaker.destroy();
			Intent intent = new Intent();
			intent.setClass(ChooseAct.this, MusicListAct.class); 
			startActivity(intent);
		}else{ 
			
			init_crate();
			if(PublicData.Choose_Speak_state == 1){
				say_info = "请告诉小老师，您是选择奥运知识学习，奥运知识竞答，还是选择奥运音乐欣赏。"; 
				speaker.startSpeaking(say_info,synthesizerListener);
				PublicData.Choose_Speak_state = 0;
			}else{
				//直接进入语音识别 
				recognizer.startListening(recognizerListener);
			}
			
		}
	}
    
    
   

	
}

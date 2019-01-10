package net_untils;

import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

public abstract class Net extends AsyncTask<String,String,String> {

	private String uri="";
	private String resposes="";
	private HttpEntity entity;
	private String sentity;
	
	/*
	 * classfy取值：
	 * 1：非常驻性网络访问任务
	 * 2：常驻性网络访问任务
	 */
	//private int classfy;
	
	public String getrespose(){
		return resposes;
		
	}
	
	public Net(String uri,HttpEntity entity,int classfy){
		this.uri=uri;
		this.entity=entity;
		//this.classfy=classfy;
		Log.i("Net内部监测", "uri="+uri+",entity="+entity.toString());
	}
	
	public Net(String uri,Object object,int classfy) throws UnsupportedEncodingException{
		this(uri, new Gson().toJson(object),classfy);
	}
	
	public Net(String uri,String sentity,int classfy) throws UnsupportedEncodingException{
		this(uri,new StringEntity(sentity),classfy);
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO 自动生成的方法存根
		
		
		try {
			//网络服务管理
			
			
			HttpPost post=new HttpPost(new URL(uri).toURI());
			post.setEntity(entity);
			HttpClient hc =new DefaultHttpClient();
			
			hc.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
			
			BufferedReader bu;
			bu = new BufferedReader(new InputStreamReader(hc.execute(post)
					.getEntity().getContent()));
			
			String line = "";
			
			while ((line = bu.readLine()) != null) {
				resposes += line;
			}
			Log.i("Net网络跟踪",resposes);
			
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			Log.i("Net内部监测", "NET内部出错");
		} 
		
		Log.i(uri+"返回检查",this.getrespose());
		return resposes;
	}
	
	@Override
	protected abstract void onPostExecute(String result);

}

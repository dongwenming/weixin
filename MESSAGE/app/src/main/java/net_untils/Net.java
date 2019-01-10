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
	 * classfyȡֵ��
	 * 1���ǳ�פ�������������
	 * 2����פ�������������
	 */
	//private int classfy;
	
	public String getrespose(){
		return resposes;
		
	}
	
	public Net(String uri,HttpEntity entity,int classfy){
		this.uri=uri;
		this.entity=entity;
		//this.classfy=classfy;
		Log.i("Net�ڲ����", "uri="+uri+",entity="+entity.toString());
	}
	
	public Net(String uri,Object object,int classfy) throws UnsupportedEncodingException{
		this(uri, new Gson().toJson(object),classfy);
	}
	
	public Net(String uri,String sentity,int classfy) throws UnsupportedEncodingException{
		this(uri,new StringEntity(sentity),classfy);
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO �Զ����ɵķ������
		
		
		try {
			//����������
			
			
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
			Log.i("Net�������",resposes);
			
		} catch (Exception e) {
			// TODO �Զ����ɵ� catch ��
			e.printStackTrace();
			Log.i("Net�ڲ����", "NET�ڲ�����");
		} 
		
		Log.i(uri+"���ؼ��",this.getrespose());
		return resposes;
	}
	
	@Override
	protected abstract void onPostExecute(String result);

}

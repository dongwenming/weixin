package net_untils;

import android.content.Context;

public class Connectionconfig {
	private Context context;
	private String ip;
	private int port;
	private int readbuffersize;
	private long connectionTimeout;

	public Context getContext() {
		return context;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}
	public int getReadbuffersize() {
		return readbuffersize;
	}

	public long getConnectionTimeout() {
		return connectionTimeout;
	}

	public static class Builder{
		private Context context;
		private String ip="";
		private int port=0;
		private int readbuffersize=10240;
		private long connectionTimeout=10000;
		
		public Builder(Context context){
			this.context=context;
		}

		public Builder setIp(String ip) {
			this.ip = ip;
			return this;
		}

		public  Builder setPort(int port) {
			this.port = port;
			return this;
		}

		public  Builder setReadbuffersize(int readbuffersize) {
			this.readbuffersize = readbuffersize;
			return this;
		}

		public  Builder setConnectionTimeout(long connectionTimeout) {
			this.connectionTimeout = connectionTimeout;
			return this;
		}
		
		private void applyconfig(Connectionconfig config){
			config.context=this.context;
			config.ip=this.ip;
			config.port=this.port;
			config.readbuffersize=this.readbuffersize;
			config.connectionTimeout=this.connectionTimeout;
		}
		
		public Connectionconfig builder(){
			Connectionconfig co=new Connectionconfig();
			applyconfig(co);
			return co;
		}
		
	}
}

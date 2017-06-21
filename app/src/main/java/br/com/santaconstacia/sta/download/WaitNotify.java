package br.com.santaconstacia.sta.download;

public class WaitNotify {

	String mMonitorObject = new String();
	boolean wasSignalled = false;
	
	public void doWait() {
		synchronized (mMonitorObject) {
			while( !wasSignalled ) {
				try {
					mMonitorObject.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			wasSignalled = false;
		}
	}
	
	public void doNotify() {
		synchronized (mMonitorObject) {
			wasSignalled = true;
			mMonitorObject.notify();
		}
	}
	
}

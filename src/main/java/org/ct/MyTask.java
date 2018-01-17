package org.ct;

import java.util.concurrent.ForkJoinTask;

class MyTask extends ForkJoinTask<String> {
	String content;
	@Override
	public String getRawResult() {
		return this.content;
	}

	@Override
	protected void setRawResult(String value) {
		this.content = value;
	}

	@Override
	protected boolean exec() {
		return false;
	}
	
}
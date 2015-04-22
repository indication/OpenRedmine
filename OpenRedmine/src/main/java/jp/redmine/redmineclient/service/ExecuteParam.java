package jp.redmine.redmineclient.service;

class ExecuteParam {
	public ExecuteParam(ExecuteMethod m, int p) {
		method = m;
		priority = p;
	}

	public ExecuteParam(ExecuteMethod m, int p, int c) {
		method = m;
		priority = p;
		connection_id = c;
	}

	public ExecuteMethod method;
	public int priority;
	public int connection_id;
	public int offset;
	public int param_int1;
	public long param_long1;
	public String param_string1;
}

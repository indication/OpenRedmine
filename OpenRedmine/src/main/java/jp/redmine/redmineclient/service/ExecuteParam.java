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
	public boolean param_bool1;
	public int param_int1;
	public long param_long1;
	public String param_string1;

	public ExecuteParam Clone() {
		ExecuteParam item = new ExecuteParam(method, priority, connection_id);
		item.offset = offset;
		item.param_bool1 = param_bool1;
		item.param_int1 = param_int1;
		item.param_long1 = param_long1;
		item.param_string1 = param_string1;
		return item;
	}
}

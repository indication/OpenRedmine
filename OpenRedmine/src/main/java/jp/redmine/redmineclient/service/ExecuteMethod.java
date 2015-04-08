package jp.redmine.redmineclient.service;

public enum ExecuteMethod {
	Halt(0),
	Flush(1),
	Master(2),
	Project(3),;

	ExecuteMethod(int nm) {
		this.id = nm;
	}

	private int id;

	public int getId() {
		return id;
	}

	public static ExecuteMethod getValueOf(int kind) {
		for (ExecuteMethod i : values()) {
			if (i.getId() == kind)
				return i;
		}
		return Halt;
	}
}

package jp.redmine.redmineclient.entity;

public class DummySelection implements IMasterRecord {
	private String name;
	private Long id;
	@Override
	public void setRemoteId(Long id) {
	}
	@Override
	public Long getRemoteId() {
		return null;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}
}
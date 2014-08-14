package jp.redmine.redmineclient.activity.pager;

import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

abstract public class CorePage<T> {
	abstract protected Fragment getRawFragment(T param);
	abstract public CharSequence getName();
	private T param;
	private WeakReference<Fragment> fragment;
	public Fragment getFragment(){
		Fragment _fragment = fragment == null ? null : fragment.get();
		if(_fragment == null){
			_fragment = getRawFragment(getParam());
			fragment = new WeakReference<Fragment>(_fragment);
		}
		return _fragment;
	}
	public CorePage<T> setParam(T p){
		param = p;
		return this;
	}
	public T getParam(){
		return param;
	}
	public Integer getIcon(){
		return null;
	}
	public boolean isDefault(){
		return false;
	}
}

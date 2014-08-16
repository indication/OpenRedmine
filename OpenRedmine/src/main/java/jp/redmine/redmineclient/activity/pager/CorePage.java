package jp.redmine.redmineclient.activity.pager;

import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

abstract public class CorePage<T> {
	abstract protected Fragment getRawFragment(T param);
	String title = "";
	Integer icon = null;
	boolean isDefault = false;
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
	public  CorePage<T> setName(String name){
		title = name;
		return this;
	}
	public CharSequence getName(){
		return title;
	}
	public  CorePage<T> setIcon(Integer ic){
		icon = ic;
		return this;
	}
	public Integer getIcon(){
		return icon;
	}
	public  CorePage<T> setDefault(boolean id){
		isDefault = id;
		return this;
	}
	public boolean isDefault(){
		return isDefault;
	}
}

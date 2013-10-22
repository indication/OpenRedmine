package jp.redmine.redmineclient.external.lib;

public class PlaceHolder <T>{
	public T item;
	public PlaceHolder(){}
	public PlaceHolder(T value){
		setItem(value);
	}
	public T getItem(){
		return item;
	}
	public void setItem(T value){
		item = value;
	}
}

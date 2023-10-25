package com.psu.scrumboard.views.components.cardandcolumn;

public interface BoardComponentUpdate<T> {

	void reload();

	void reload(T data);

}
package com.psu.scrumboard.data.utils;

import java.util.List;

public interface ScrumCrud<T> {

	public Class<T> getType();

	public T create(T data);

	public T findById(String id);

	public List<T> list();

	public void deleteById(String id);

	public T update(T data);

}

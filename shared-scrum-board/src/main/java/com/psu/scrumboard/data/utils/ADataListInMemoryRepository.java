package com.psu.scrumboard.data.utils;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class ADataListInMemoryRepository<T extends ScrumDataId> implements ScrumCrud<T> {

	@Getter
	private Class<T> type = null;
	private List<T> items = Lists.newArrayList();

	public ADataListInMemoryRepository(@NonNull Class<T> type) {
		this.type = type;
	}

	@Override
	public T findById(String id) {
		return items.stream().filter(Objects::nonNull).filter(i -> StringUtils.equals(i.getId(), id)).findAny()
				.orElse(null);
	}

	@Override
	public List<T> list() {
		return items;
	}

	@Override
	public void deleteById(String id) {
		items.removeIf(i -> StringUtils.equals(i.getId(), id));
	}

	@Override
	public T update(T bean) {
		if (bean != null) {
			deleteById(bean.getId());

			if (items.add(bean)) {
				return bean;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public T create(T data) {
		if (data != null) {
			if (!items.contains(data)) {
				items.add(data);
				log.info("Stored Data: " + items.size());
				return data;
			} else {
				log.info("Stored Data: " + items.size());
				return null;
			}
		} else {
			return null;
		}

	}

}

package com.psu.scrumboard.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.psu.scrumboard.utils.Utils;
import com.google.common.collect.Lists;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = { "id" })
public class ScrumPollData {

	@Builder.Default
	private String id = Utils.randomId();

	private String text;

	@Builder.Default
	private List<ScrumPollItem> items = Lists.newArrayList();

	public ScrumPollItem getItemById(String id) {
		return items.stream().filter(e -> StringUtils.equals(e.getId(), id)).findFirst().orElse(null);
	}

}

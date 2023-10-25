package com.psu.scrumboard.model;

import java.util.List;

import com.google.common.collect.Lists;
import com.psu.scrumboard.data.interfaces.ScrumBoardLikeService;
import com.psu.scrumboard.data.table.ScrumCardLikes;
import com.psu.scrumboard.utils.Utils;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@EqualsAndHashCode(of = { "id" })
public class ScrumPollItem implements ScrumBoardLikeService {

	@Builder.Default
	private String id = Utils.randomId();

	private String text;

	@Builder.Default
	private List<ScrumCardLikes> likes = Lists.newArrayList();

}

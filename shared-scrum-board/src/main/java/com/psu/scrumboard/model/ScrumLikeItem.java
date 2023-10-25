package com.psu.scrumboard.model;

import java.util.List;

import com.psu.scrumboard.data.interfaces.ScrumBoardLikeService;
import com.psu.scrumboard.data.table.ScrumCardLikes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScrumLikeItem implements ScrumBoardLikeService { //LikeItem

	private String id;
	private List<ScrumCardLikes> likes;

}

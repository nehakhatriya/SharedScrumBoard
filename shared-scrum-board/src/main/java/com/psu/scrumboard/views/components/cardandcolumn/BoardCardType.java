package com.psu.scrumboard.views.components.cardandcolumn;

import java.util.List;

import com.psu.scrumboard.data.table.ScrumCardComment;
import com.psu.scrumboard.views.components.interfaces.ComponentInterface;

public interface BoardCardType<Update> extends BoardComponentUpdate<Update>, ComponentInterface {

	public void changeText(String text);

	public void changeButtonCommentsCaption(List<ScrumCardComment> set);

}

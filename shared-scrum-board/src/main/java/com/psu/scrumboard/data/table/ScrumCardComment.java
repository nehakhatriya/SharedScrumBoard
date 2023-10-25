package com.psu.scrumboard.data.table;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.psu.scrumboard.utils.Utils;
import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ScrumCardComment implements ScrumBoardPosition {

	@Id
	@Builder.Default
	private String commentId = Utils.randomId();

	@Builder.Default
	private int scrumPosition = -1;
	
	private String id;

	@Lob
	private String text;

	@Override
	public int getPosition() {
		// TODO Auto-generated method stub
		return 0;
	}

}

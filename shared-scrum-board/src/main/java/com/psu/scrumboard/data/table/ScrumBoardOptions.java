package com.psu.scrumboard.data.table;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.psu.scrumboard.data.utils.ScrumDataId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(of = { "id" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ScrumBoardOptions implements ScrumDataId, Serializable {

	private static final long serialVersionUID = 3951230846648440224L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private int maxColumns;
	private int maxCards;
	private int maxCardTextLength;
	@Builder.Default
	private int maxLikesPerUserPerCard = 1;
	@Builder.Default
	private boolean cardSortDirectionDesc = true;
}

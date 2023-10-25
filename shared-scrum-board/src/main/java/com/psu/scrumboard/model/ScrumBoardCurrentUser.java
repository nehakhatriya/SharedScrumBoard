package com.psu.scrumboard.model;


import java.io.Serializable;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(of = { "id" })
@Builder
public class ScrumBoardCurrentUser implements Serializable {

	private static final long serialVersionUID = -1403193875800661438L;

	private String id;
	private boolean active;

}

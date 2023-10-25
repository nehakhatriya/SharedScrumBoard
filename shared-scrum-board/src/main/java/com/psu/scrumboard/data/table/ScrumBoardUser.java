package com.psu.scrumboard.data.table;


import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import com.google.common.collect.Lists;
import com.psu.scrumboard.config.ScrumUserListJsonTransformer;
import com.psu.scrumboard.data.utils.ScrumDataId;
import com.psu.scrumboard.model.ScrumBoardCurrentUser;

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
public class ScrumBoardUser implements ScrumDataId, Serializable {

	private static final long serialVersionUID = -3903314185609358126L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	@Lob
	@Convert(converter = ScrumUserListJsonTransformer.class)
	private List<ScrumBoardCurrentUser> users;

	
	public List<ScrumBoardCurrentUser> getUsers() {
		if(users == null) {
			users = Lists.newArrayList();
		}
		
		return users;
	}
}

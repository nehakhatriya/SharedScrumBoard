package com.psu.scrumboard.data.table;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
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
public class ScrumUser implements Serializable {
	private static final long serialVersionUID = -3903314185609358126L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	@Lob
	private String username;

	@Lob
	private String password;

}

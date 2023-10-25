package com.psu.scrumboard.data.table;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import org.apache.commons.collections4.CollectionUtils;

import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.google.common.collect.Sets;

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
public class ScrumColumn implements Serializable, ScrumBoardPosition {

	private static final long serialVersionUID = 5307688703528077543L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String ownerId;

	private String name;

	private String description;

	@Builder.Default
	private int position = -1;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "columnId")
	@Builder.Default
	private Set<ScrumCard> cards = Sets.newHashSet();

	public ScrumCard getCardById(String id) {
		if (CollectionUtils.isEmpty(cards)) {
			return null;
		}

		return cards.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	public boolean addCard(ScrumCard note) {
		return cards.add(note);
	}

	public boolean removeCardById(String id) {
		return cards.removeIf(e -> e.getId().equals(id));
	}

}

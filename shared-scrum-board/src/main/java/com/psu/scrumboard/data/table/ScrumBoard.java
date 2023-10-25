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
import javax.persistence.OneToOne;

import org.apache.commons.collections4.CollectionUtils;

import com.google.common.collect.Sets;
import com.psu.scrumboard.data.utils.ScrumDataId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

@Data
@EqualsAndHashCode(of = { "id" })
@ToString(exclude = { "columns", "user" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ScrumBoard implements ScrumDataId, Serializable {

	private static final long serialVersionUID = 3523289407526253761L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String ownerId;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private ScrumBoardOptions options = ScrumBoardOptions.builder().build();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private ScrumBoardUser user = ScrumBoardUser.builder().build();

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "dataId")
	@Builder.Default
	private Set<ScrumColumn> columns = Sets.newHashSet();

	public void resetLikes() {
		// columns.stream().flatMap(e -> e.getCards().stream()).forEach(e -> e.getByType(ILike.class).clear());
	}

	public ScrumColumn getColumnById(@NonNull ScrumColumn projectDataColumn) {
		return getColumnById(projectDataColumn.getId());
	}

	public ScrumColumn getColumnById(String id) {
		return columns.stream().filter(e -> e.getId().equals(id)).findFirst().orElse(null);
	}

	public ScrumBoard addColumn(@NonNull ScrumColumn column) {
		columns.add(column);
		return this;
	}

	public ScrumBoard addCard(String columnId, ScrumCard note) {
		ScrumColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.addCard(note);
		return this;
	}

	public ScrumBoard removeCardById(String columnId, String cardId) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		ScrumColumn pdc = getColumnById(columnId);
		if (pdc == null) {
			return this;
		}

		pdc.removeCardById(cardId);
		return this;
	}

	public boolean removeColumn(ScrumColumn column) {
		return columns.remove(column);
	}

	public ScrumBoard removeColumnById(String id) {
		if (CollectionUtils.isEmpty(columns)) {
			return this;
		}

		columns.removeIf(e -> e.getId().equals(id));
		return this;
	}

}

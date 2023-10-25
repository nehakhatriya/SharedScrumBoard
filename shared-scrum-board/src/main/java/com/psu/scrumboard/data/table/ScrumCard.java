package com.psu.scrumboard.data.table;

import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.psu.scrumboard.enums.ScrumCardType;
import com.psu.scrumboard.model.ScrumTextItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
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
public class ScrumCard implements Serializable, ScrumBoardPosition {

	private static final long serialVersionUID = 652620276690725942L;

	@Id
	@Builder.Default
	private String id = UUID.randomUUID().toString();

	private String ownerId;

	@Builder.Default
	private int getScrumPosition = -1;

	@Builder.Default
	private ScrumCardType type = ScrumCardType.TextComponentCard;

	@Lob
	private String text;

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "cardId")
	@Builder.Default
	private List<ScrumCardComment> comments = new ArrayList();

	public int getLikes() {
		return getByType(ScrumTextItem.class).orElse(ScrumTextItem.builder().build()).countAllLikes();
	}

	public void setTextByType(Object o) {
		setText(new Gson().toJson(o));
	}

	public <T> Optional<T> getByType(Class<T> type) {
		T data = null;
		try {
			data = new Gson().fromJson(text, type);
		} catch (Exception e) {
		}

		return Optional.ofNullable(data);
	}

	public <T> Optional<List<T>> getByTypeAsList(Class<T> type) {
		List<T> data = null;
		try {
			data = new Gson().fromJson(text, TypeToken.getParameterized(ArrayList.class, type).getType());
		} catch (Exception e) {
		}

		return Optional.ofNullable(data);
	}

	@Override
	public int getPosition() {
		return 0;
	}

}

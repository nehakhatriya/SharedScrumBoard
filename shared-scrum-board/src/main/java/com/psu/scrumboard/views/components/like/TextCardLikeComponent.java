package com.psu.scrumboard.views.components.like;

import java.util.function.UnaryOperator;

import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardLikes;
import com.psu.scrumboard.model.ScrumTextItem;
import com.psu.scrumboard.session.SessionUtils;
import com.psu.scrumboard.stream.ScrumCardStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.cardandcolumn.BoardComponentUpdate;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class TextCardLikeComponent extends VerticalLayout implements BoardComponentUpdate<ScrumCard> {

	private static final long serialVersionUID = -2483871323771596716L;

	private ScrumView view;

	private ScrumCardRepository repository = SpringContext.getBean(ScrumCardRepository.class);
	private String boardId;
	private String cardId;

	private Button btnLike;
	private Button btnRemoveLike;

	public TextCardLikeComponent(ScrumView view, String boardId, String cardId) {
		this.view = view;
		this.boardId = boardId;
		this.cardId = cardId;
		setId(cardId);
		Icon like=VaadinIcon.THUMBS_UP_O.create();
		like.getStyle().set("color", "#008CBA");
		btnLike = new Button(like);
		btnLike.setIcon(like);
		ToolTip.add(btnLike, "Like the card");
		btnLike.setText(String.valueOf(getCurrentLikes()));
		btnLike.getStyle().set("color", "#008CBA");
		btnLike.setWidthFull();
		btnLike.addClickListener(e -> {
			if (islikeLimitAlreadyExistsByOwner()) {
				Notification.show("You already liked the card", ScrumConfig.NOTIF_TIME, Position.MIDDLE);
				return;
			}
			addLike();
			ScrumCardStream.broadcast(cardId, "update");
		});
		add(btnLike);
		Icon dislike = VaadinIcon.THUMBS_DOWN_O.create();
		dislike.getStyle().set("color", "#008CBA");
		btnRemoveLike = new Button(dislike);
		ToolTip.add(btnRemoveLike, "Remove your like");
		btnRemoveLike.setText(String.valueOf(getCurrentLikesByOwner()));
		btnRemoveLike.getStyle().set("color", "#008CBA");
		btnRemoveLike.setWidthFull();
		btnRemoveLike.addClickListener(e -> {
			if (!isLikedByOwner()) {
				Notification.show("You must like the card, before removing your like", ScrumConfig.NOTIF_TIME, Position.MIDDLE);
				return;
			}

			removeLike();
			ScrumCardStream.broadcast(cardId, "update");
		});
		add(btnRemoveLike);
		setMargin(false);
		setPadding(false);
		setSpacing(false);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
	}

	public boolean islikeLimitAlreadyExistsByOwner() {
		if (view.getOptions().getMaxLikesPerUserPerCard() == 0) {
			return false;
		}

		return repository.findById(cardId).get().getByType(ScrumTextItem.class)
				.orElseGet(() -> ScrumTextItem.builder().build()).getLikes().stream()
				.filter(e -> e.getOwnerId().equals(SessionUtils.getSessionId()))
				.count() >= view.getOptions().getMaxLikesPerUserPerCard();
	}

	public int getCurrentLikes() {
		ScrumCard tmp = repository.findById(cardId).get();
		return tmp.getByType(ScrumTextItem.class).orElseGet(() -> ScrumTextItem.builder().build()).countAllLikes();
	}

	public int getCurrentLikesByOwner() {
		ScrumCard tmp = repository.findById(cardId).get();
		return tmp.getByType(ScrumTextItem.class).orElseGet(() -> ScrumTextItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId());
	}

	public void addLike() {
		updateItem(e -> {
			e.getLikes().add(ScrumCardLikes.builder().ownerId(SessionUtils.getSessionId()).likeValue(1).build());
			return e;
		});
	}

	public void removeLike() {
		updateItem(e -> {
			e.removeLikeByOwnerId(SessionUtils.getSessionId());
			return e;
		});
	}

	public void updateItem(UnaryOperator<ScrumTextItem> update) {
		ScrumCard tmp = getCard();
		ScrumTextItem item = getItem(tmp);
		tmp.setTextByType(update.apply(item));
		repository.save(tmp);
	}

	public ScrumTextItem getItem(ScrumCard card) {
		return card.getByType(ScrumTextItem.class).get();
	}

	public ScrumTextItem getItem() {
		ScrumCard tmp = repository.findById(cardId).get();
		return getItem(tmp);
	}

	public ScrumCard getCard() {
		return repository.findById(cardId).get();
	}

	private boolean isLikedByOwner() {
		ScrumCard tmp = repository.findById(cardId).get();
		return tmp.getByType(ScrumTextItem.class).orElseGet(() -> ScrumTextItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0;
	}

	public void changeText(int likes) {
		if (!btnLike.getText().equals(String.valueOf(likes))) {
			btnLike.setText(String.valueOf(likes));
		}

		btnRemoveLike.setText(String.valueOf(getCurrentLikesByOwner()));
	}

	private void changeButtonIconToLiked(boolean liked) {
		if (liked) {
			Icon like = VaadinIcon.THUMBS_UP_O.create();
			like.getStyle().set("color", "#008CBA");
			btnLike.setIcon(like);
		} else {
			Icon dislike = VaadinIcon.THUMBS_UP_O.create();
			dislike.getStyle().set("color", "#008CBA");
			btnLike.setIcon(dislike);
		}
	}

	public void reload() {
		ScrumCard tmp = repository.findById(cardId).get();
		reload(tmp);
	}

	@Override
	public void reload(ScrumCard data) {
		changeText(
				data.getByType(ScrumTextItem.class).orElseGet(() -> ScrumTextItem.builder().build()).countAllLikes());

		changeButtonIconToLiked(data.getByType(ScrumTextItem.class).orElseGet(() -> ScrumTextItem.builder().build())
				.cardLikesByOwnerId(SessionUtils.getSessionId()) != 0);
	}
}

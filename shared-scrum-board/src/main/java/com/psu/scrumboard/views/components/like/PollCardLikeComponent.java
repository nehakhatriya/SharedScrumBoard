package com.psu.scrumboard.views.components.like;

import java.util.Collection;
import java.util.function.UnaryOperator;
import com.google.common.collect.Lists;
import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardLikes;
import com.psu.scrumboard.model.ScrumPollData;
import com.psu.scrumboard.model.ScrumPollItem;
import com.psu.scrumboard.stream.ScrumCardStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.cardandcolumn.BoardComponentUpdate;
import com.psu.scrumboard.session.SessionUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class PollCardLikeComponent extends HorizontalLayout implements BoardComponentUpdate<ScrumCard> {

	private static final long serialVersionUID = -2483871323771596716L;

	private ScrumView view;

	private ScrumCardRepository repository = SpringContext.getBean(ScrumCardRepository.class);
	private String boardId;
	private String pollId;
	private String cardId;

	private Button btnLike;
	private Button btnRemoveLike;

	public PollCardLikeComponent(ScrumView view, String boardId, String cardId, String pollId) {
		this.view = view;
		this.boardId = boardId;
		this.pollId = pollId;
		this.cardId = cardId;
		setId(pollId);
		
		Icon like = VaadinIcon.THUMBS_UP_O.create();
		btnLike = new Button(like);
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
				Notification.show("You must like the card, before removing your like", ScrumConfig.NOTIF_TIME,
						Position.MIDDLE);
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

		ScrumCard tmp = repository.findById(cardId).get();
		return tmp.getByTypeAsList(ScrumPollItem.class).orElseGet(() -> Lists.newArrayList()).stream()
				.map(ScrumPollItem::getLikes).flatMap(Collection::stream)
				.filter(e -> e.getOwnerId().equals(SessionUtils.getSessionId()))
				.count() >= view.getOptions().getMaxLikesPerUserPerCard();
	}

	public int getCurrentLikes() {
		ScrumCard tmp = repository.findById(cardId).get();
		ScrumPollItem item = tmp.getByType(ScrumPollItem.class).orElseGet(() -> ScrumPollItem.builder().build());
		return item != null ? item.countAllLikes() : null;
	}

	public int getCurrentLikesByOwner() {
		return getItem().cardLikesByOwnerId(SessionUtils.getSessionId());
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

	private boolean isLikedByOwner() {
		return getItem().cardLikesByOwnerId(SessionUtils.getSessionId()) != 0;
	}

	public void updateItem(UnaryOperator<ScrumPollItem> update) {
		ScrumCard tmp = getCard();
		ScrumPollData data = tmp.getByType(ScrumPollData.class).get();
		ScrumPollItem item = data.getItemById(pollId);
		item = update.apply(item);
		tmp.setTextByType(data);
		repository.save(tmp);
	}

	public ScrumPollItem getItem(ScrumCard card) {
		return card.getByType(ScrumPollData.class).get().getItemById(pollId);
	}

	public ScrumPollItem getItem() {
		ScrumCard tmp = repository.findById(cardId).get();
		return getItem(tmp);
	}

	public ScrumCard getCard() {
		return repository.findById(cardId).get();
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
		reload(repository.findById(cardId).get());
	}

	@Override
	public void reload(ScrumCard data) {
		changeText(getItem(data).countAllLikes());
		changeButtonIconToLiked(getItem(data).cardLikesByOwnerId(SessionUtils.getSessionId()) != 0);
	}

}

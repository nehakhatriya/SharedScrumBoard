package com.psu.scrumboard.views.components.cardandcolumn;

import java.util.List;

import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.repository.ScrumColumnRepository;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardComment;
import com.psu.scrumboard.data.table.ScrumColumn;
import com.psu.scrumboard.stream.ScrumCardStream;
import com.psu.scrumboard.stream.ScrumColumnStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.interfaces.BroadcastRegistryInterface;
import com.psu.scrumboard.views.components.interfaces.ComponentInterface;
import com.psu.scrumboard.views.utils.ScrumBoardViewUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dnd.DragSource;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@CssImport(value = "./styles/card-style.css", themeFor = "vaadin-horizontal-layout")
public class CardComponent extends HorizontalLayout implements ComponentInterface, BroadcastRegistryInterface {

	private static final long serialVersionUID = -1213748155629932731L;

	private ScrumCardRepository cardRepository = SpringContext.getBean(ScrumCardRepository.class);
	private ScrumColumnRepository columnRepository = SpringContext.getBean(ScrumColumnRepository.class);

	@Getter
	private ColumnComponent column;

	@Getter
	private String columnId;

	@Getter
	private String cardId;

	@Getter
	private ScrumView view;
	private H3 h3;
	private BoardCardType template;
	private String ownerid;
	public CardComponent(ScrumView view, ColumnComponent column, String columnId, ScrumCard card) {
		this.columnId = columnId;
		this.column = column;
		this.view = view;
		setId(card.getId());
		cardId = getId().get();
		ownerid=card.getOwnerId();
		setSpacing(true);
		setPadding(true);
		setMargin(false);

		switch (card.getType()) {
		case TextComponentCard:
			template = new BoardTextCard(this);
			break;

		case PollCard:
			template = new BoardPollCard(this);
			break;

		default:
			log.error("failed to find template for card type: {}", card.getType());
			break;
		}

		DragSource<CardComponent> dragScrumConfig = DragSource.create(this);
		dragScrumConfig.addDragStartListener(e -> {
			if (ScrumConfig.DEBUG) {
				Notification.show("Start Drag Card: " + e.getComponent().getCard().getText());
			}

			e.setDragData(getId().get());
		});

		dragScrumConfig.addDragEndListener(e -> {
			if (!e.isSuccessful()) {
				Notification.show("Please move the card to a column", 3000, Position.MIDDLE);
				return;
			}

			if (ScrumConfig.DEBUG) {
				Notification.show("Stop drag Card: " + e.getComponent().getCard().getText());
			}

			Notification.show("Card moved", 3000, Position.BOTTOM_END);
		});
	}

	public ScrumCard getCard() {
		return cardRepository.findById(getCardId()).get();
	}

	public void deleteCard() {
		log.info("delete card: " + getId().get());
		ScrumColumn c = columnRepository.findById(column.getId().get()).get();
		if(ScrumBoardViewUtils.isAllowed(view.getOwnerId(), ownerid)) {
			c.removeCardById(getId().get());
		}
		else {
			Notification.show("You are not allowed to delete the card ");
		}
		columnRepository.save(c);
		ScrumColumnStream.broadcast(column.getId().get(), "update");
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		registerBroadcast("card", ScrumCardStream.register(getId().get(), event -> {
			ui.access(() -> {
				if (ScrumConfig.DEBUG) {
					Notification.show("receiving broadcast for update", ScrumConfig.NOTIF_TIME,
							Position.BOTTOM_END);
				}
				reload();
			});
		}));
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		unRegisterBroadcasters();
	}

	public void changeText(String text) {
		template.changeText(text);
	}

	public void changeButtonCommentsCaption(List<ScrumCardComment> set) {
		template.changeButtonCommentsCaption(set);
	}

	public void reload() {
		template.reload();
	}

}

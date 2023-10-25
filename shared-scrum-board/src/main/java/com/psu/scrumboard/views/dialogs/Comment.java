package com.psu.scrumboard.views.dialogs;

import java.util.Comparator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardComment;
import com.psu.scrumboard.stream.ScrumCardCommentStream;
import com.psu.scrumboard.stream.ScrumCardStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.like.CommentComponent;
import com.psu.scrumboard.views.utils.ScrumBoardViewUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Comment extends Dialog {

	private static final long serialVersionUID = -2119496244059224808L;

	private ScrumCardRepository cardRepository = SpringContext.getBean(ScrumCardRepository.class);

	private Registration broadcasterRegistration;

	private VerticalLayout root;
	private VerticalLayout commentsLayout;

	private String cardId;
	private String cardText;

	private Label title;

	public Comment(String cardId, String cardText) {
		this.cardId = cardId;
		this.cardText = cardText;

		setId(cardId);
		
		root = new VerticalLayout();
		root.setSizeFull();
		add(root);

		HorizontalLayout header = new HorizontalLayout();
		header.setPadding(true);
		header.setAlignItems(Alignment.CENTER);
		header.getStyle().set("border", "1px solid black");
		header.setWidthFull();
		root.add(header);

		title = new Label();
		title.setWidthFull();
		header.add(title);
		Icon add=VaadinIcon.PLUS.create();
		add.getStyle().set("color", "#008CBA");
		Button btn = new Button(add);
		ToolTip.add(btn, "Add Comment to Card");
		btn.addClickListener(e -> {
			new ScrumTextArea("Write Comment", Strings.EMPTY, savedText -> {
				ScrumCard tmp = cardRepository.findById(getId().get()).get();
				addComment(ScrumCardComment.builder().text(savedText).scrumPosition(ScrumBoardViewUtils.calculateNextPosition(tmp.getComments())).build());
			}).open();
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.END);
		btnLayout.add(btn);
		header.add(btnLayout);

		setWidth("500px");
		setHeight("400px");

		commentsLayout = new VerticalLayout();
		commentsLayout.setWidthFull();
		commentsLayout.setMargin(false);
		commentsLayout.setPadding(false);
		root.add(commentsLayout);

		initCards();
	}

	private void addTitle(int comments, String cardText) {
		title.setText(String.format("There are '%s' Comments for Card: %s", comments, StringUtils.abbreviate(cardText, 15)));
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		broadcasterRegistration = ScrumCardCommentStream.register(getId().get(), event -> {
			ui.access(() -> {
				if (ScrumConfig.DEBUG) {
					Notification.show("receiving broadcast for update", ScrumConfig.NOTIF_TIME, Position.BOTTOM_END);
				}

				reload();
			});
		});
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		if (broadcasterRegistration != null) {
			broadcasterRegistration.remove();
			broadcasterRegistration = null;
		} else {
			log.info("cannot remove broadcast, because it is null");
		}
	}

	private void initCards() {
		commentsLayout.removeAll();
		ScrumCard card = cardRepository.findById(cardId).get();
		addTitle(CollectionUtils.size(card.getComments()), cardText);
		card.getComments().stream().sorted(Comparator.comparing(ScrumBoardPosition::getPosition).reversed()).forEach(e -> commentsLayout.add(new CommentComponent(cardId, e)));
	}

	private void addComment(ScrumCardComment cardComment) {
		commentsLayout.addComponentAsFirst(new CommentComponent(cardId, cardComment));
		ScrumCard card = cardRepository.findById(cardId).get();
		card.getComments().add(cardComment);
		cardRepository.save(card);
		ScrumCardCommentStream.broadcast(cardId, "update");
		ScrumCardStream.broadcast(cardId, "update");
	}

	public void reload() {
		initCards();
	}

}

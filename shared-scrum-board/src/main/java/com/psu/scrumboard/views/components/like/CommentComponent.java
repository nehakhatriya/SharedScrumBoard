package com.psu.scrumboard.views.components.like;


import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.repository.ScrumCardCommentRepository;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardComment;
import com.psu.scrumboard.stream.ScrumCardCommentStream;
import com.psu.scrumboard.stream.ScrumCardStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.dialogs.ScrumTextArea;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.extern.log4j.Log4j2;

@Log4j2
@CssImport(value = "./styles/card-style.css", themeFor = "vaadin-horizontal-layout")
public class CommentComponent extends HorizontalLayout {

	private static final long serialVersionUID = -1213748155629932731L;

	private ScrumCardRepository cardRepository = SpringContext.getBean(ScrumCardRepository.class);
	private ScrumCardCommentRepository cardCommentRepository = SpringContext.getBean(ScrumCardCommentRepository.class);

	private String cardId;
	private ScrumCardComment comment;
	private Label label;

	public CommentComponent(String cardId, ScrumCardComment comment) {
		this.comment = comment;
		this.cardId = cardId;

		setId(comment.getCommentId());
		setSpacing(true);
		setPadding(true);
		setMargin(false);
		label = new Label();
		label.getStyle().set("word-break", "break-word");
		label.setWidthFull();
		changeText(comment.getText());
		add(label);

		VerticalLayout btnLayout = new VerticalLayout();
		btnLayout.setWidth("unset");
		btnLayout.setSpacing(false);
		btnLayout.setMargin(false);
		btnLayout.setPadding(false);
		add(btnLayout);

		setFlexGrow(1, label);
		setWidthFull();
		getStyle().set("box-shadow", "0.5px solid black");
		getStyle().set("border-radius", "1em");
		getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		addClassName("card-hover");

		Button btnDelete = new Button(VaadinIcon.TRASH.create());
		ToolTip.add(btnDelete, "Delete the comment");
		btnDelete.addClickListener(e -> delete());
		btnLayout.add(btnDelete);

		label.getElement().addEventListener("click", e -> {
			new ScrumTextArea("Edit Text", label.getText(), savedText -> {
				log.info("edit comment: " + getId().get());
				ScrumCardComment c = cardCommentRepository.findById(comment.getId()).get();
				c.setText(savedText);
				cardCommentRepository.save(c);
				ScrumCardCommentStream.broadcast(cardId, "update");
				ScrumCardStream.broadcast(cardId, "update");
			}).open();
		});

		Icon editIcon = VaadinIcon.EDIT.create();
		add(editIcon);
	}

	public void delete() {
		log.info("delete card comment: {}", getId().get());
		ScrumCardComment cc = cardCommentRepository.findById(getId().get()).get();
		ScrumCard c = cardRepository.findById(cardId).get();
		c.getComments().remove(cc);
		cardRepository.save(c);
		ScrumCardCommentStream.broadcast(cardId, "update");
		ScrumCardStream.broadcast(cardId, "update");
	}

	public void reload() {
		ScrumCardComment tmp = cardCommentRepository.findById(getId().get()).get();

		// update layout with new missing data
		changeText(tmp.getText());
	}

	private void changeText(String text) {
		if (!label.getText().equals(text)) {
			label.setText(text);
		}

		if (ScrumConfig.DEBUG) {
			label.setText(text + " (" + comment.getScrumPosition() + ")");
		}
	}

}

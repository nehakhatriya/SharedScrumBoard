package com.psu.scrumboard.views.components.cardandcolumn;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.table.ScrumBoard;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardComment;
import com.psu.scrumboard.model.ScrumTextItem;
import com.psu.scrumboard.stream.ScrumCardStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.like.TextCardLikeComponent;
import com.psu.scrumboard.views.dialogs.Comment;
import com.psu.scrumboard.views.dialogs.ScrumTextArea;
import com.psu.scrumboard.views.utils.ScrumBoardViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BoardTextCard implements BoardCardType<ScrumCard> {

	private ScrumCardRepository cardRepository = SpringContext.getBean(ScrumCardRepository.class);
	private CardComponent root;

	@Getter
	private ScrumCard card;
	@Getter
	private ScrumBoard board;

	private ColumnComponent column;
	private TextCardLikeComponent likeComponent;
	private String cardId;
	private Label label;

	private Button btnComment;
	private ScrumView view;

	public BoardTextCard(CardComponent root) {
		this.root = root;
		this.card = root.getCard();
		this.view = root.getView();
		this.column = root.getColumn();
		this.cardId = root.getCardId();

		label = new Label();
		label.getStyle().set("word-break", "break-word");
		changeText(card.getText());
		label.setWidthFull();
		root.add(label);

		VerticalLayout btnLayout = new VerticalLayout();
		btnLayout.setWidth("unset");
		btnLayout.setSpacing(false);
		btnLayout.setMargin(false);
		btnLayout.setPadding(false);
		root.add(btnLayout);

		likeComponent = new TextCardLikeComponent(view, view.getId().get(), card.getId());
		btnLayout.add(likeComponent);
		Icon comment=VaadinIcon.COMMENT_O.create();
		comment.getStyle().set("color", "#008CBA");
		btnComment = new Button(comment);
		changeButtonCommentsCaption(card.getComments());
		btnComment.addClickListener(e -> {
			new Comment(cardId, label.getText()).open();
		});

		btnLayout.add(btnComment);

		if (ScrumBoardViewUtils.isAllowed(view.getOwnerId(), card.getOwnerId())) {
			Icon trash=VaadinIcon.TRASH.create();
			trash.getStyle().set("color", "#008CBA");
			Button btnDelete = new Button(trash);
			ToolTip.add(btnDelete, "Delete the card");
			btnDelete.addClickListener(e -> root.deleteCard());
			btnLayout.add(btnDelete);
		}

		root.setFlexGrow(1, label);
		root.setWidthFull();
		root.getStyle().set("box-shadow", "0.5px solid black");
		root.getStyle().set("border-radius", "1em");
		root.getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		root.addClassName("card-hover");

		if (ScrumBoardViewUtils.isAllowed(view.getOwnerId(), card.getOwnerId())) {

		
			label.getElement().addEventListener("click", e -> {
				new ScrumTextArea("Edit Text", label.getText(), savedText -> {
					log.info("edit card: " + root.id());
					ScrumCard c = cardRepository.findById(cardId).get();
					ScrumTextItem item = c.getByType(ScrumTextItem.class).get();
					item.setText(savedText);
					c.setTextByType(item);
					cardRepository.save(c);
					ScrumCardStream.broadcast(cardId, "update");
				}).open();
			});

			Icon editIcon = VaadinIcon.EDIT.create();
			editIcon.getStyle().set("color", "#008CBA");
			root.add(editIcon);
		}
	}

	@Override
	public void changeText(String text) {
		if (!label.getText().equals(text)) {
			label.setText(text);
		}

		if (ScrumConfig.DEBUG) {
			label.setText(text + " (" + card.getGetScrumPosition() + ")");
		}
	}

	@Override
	public void changeButtonCommentsCaption(List<ScrumCardComment> set) {
		if (CollectionUtils.size(set) > 0) {
			btnComment.setText(String.valueOf(set.size()));
			Icon comment=VaadinIcon.COMMENT.create();
			comment.getStyle().set("color", "#008CBA");
			btnComment.setIcon(comment);

			ToolTip.addLines(btnComment, set.stream()
					.sorted(Comparator.comparing(ScrumBoardPosition::getPosition).reversed())
					.map(ScrumCardComment::getText)
					.collect(Collectors.toList()));
		} else {
			Icon comment=VaadinIcon.COMMENT.create();
			comment.getStyle().set("color", "#008CBA");
			btnComment.setIcon(comment);
			ToolTip.add(btnComment, "Comment the Card");
		}
	}

	@Override
	public void reload() {
		card = cardRepository.findById(root.id()).get();
		reload(card);
	}

	@Override
	public void reload(ScrumCard data) {
		if (!label.getText().equals(card.getByType(ScrumTextItem.class).get().getText()))
			changeText(data.getByType(ScrumTextItem.class).get().getText());

		likeComponent.reload();

		changeButtonCommentsCaption(data.getComments());
	}

}

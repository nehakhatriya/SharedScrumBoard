package com.psu.scrumboard.views.components.cardandcolumn;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumCardComment;
import com.psu.scrumboard.model.ScrumPollData;
import com.psu.scrumboard.model.ScrumPollItem;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.like.PollCardLikeComponent;
import com.psu.scrumboard.views.utils.ScrumBoardViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BoardPollCard implements BoardCardType<ScrumCard> {

	private ScrumCardRepository cardRepository = SpringContext.getBean(ScrumCardRepository.class);

	private CardComponent root;

	@Getter
	private ScrumCard card;

	private ColumnComponent column;
	private String columnId;
	private String cardId;

	private Button btnComment;
	private ScrumView view;

	private ScrumPollData data;
	private VerticalLayout rootLayout;
	private VerticalLayout itemLayout;

	public BoardPollCard(CardComponent root) {
		this.root = root;
		this.card = root.getCard();
		this.cardId = card.getId();
		this.view = root.getView();
		this.column = root.getColumn();
		this.data = card.getByType(ScrumPollData.class).get();

		rootLayout = new VerticalLayout();
		addStyles(root, rootLayout);

		Label label = new Label(data.getText());
		Icon check = VaadinIcon.TRASH.create();
		check.getStyle().set("color", "#008CBA");
		Button btnDelete = new Button(check);
		ToolTip.add(btnDelete, "Delete the card");
		btnDelete.addClickListener(e -> root.deleteCard());

		HorizontalLayout layoutText = new HorizontalLayout(label);
		layoutText.setWidthFull();
		layoutText.setPadding(true);
		layoutText.setSpacing(false);
		layoutText.setMargin(false);

		HorizontalLayout layoutButton = new HorizontalLayout();
		layoutButton.setPadding(false);
		layoutButton.setSpacing(false);
		layoutButton.setMargin(false);

		if (ScrumBoardViewUtils.isAllowed(view.getOwnerId(), card.getOwnerId())) {
			Icon trash = VaadinIcon.TRASH.create();
			trash.getStyle().set("color", "#008CBA");
			Button btnDeletee = new Button(trash);
			ToolTip.add(btnDeletee, "Delete the card");
			btnDeletee.addClickListener(e -> root.deleteCard());
			layoutButton.add(btnDeletee);

		}
		HorizontalLayout layoutTitle = new HorizontalLayout(layoutText, layoutButton);
		layoutTitle.setJustifyContentMode(JustifyContentMode.START);
		layoutTitle.setPadding(false);
		layoutTitle.setSpacing(false);
		layoutTitle.setMargin(false);
		layoutTitle.getStyle().set("box-shadow", "rgb(33, 33, 33) 0px 1px 5px 3px");
		layoutTitle.setWidthFull();
		rootLayout.add(layoutTitle);
		itemLayout = new VerticalLayout();
		itemLayout.setMargin(false);
		itemLayout.setPadding(false);
		itemLayout.setSpacing(false);
		itemLayout.setWidthFull();
		rootLayout.add(itemLayout);

		if (CollectionUtils.isNotEmpty(data.getItems())) {
			data.getItems().stream().forEachOrdered(e -> {
				itemLayout.add(addItem(e));
			});
		}
	}

	public VerticalLayout addItem(ScrumPollItem item) {
		VerticalLayout layout = new VerticalLayout();
		layout.getStyle().set("border", "0.5px solid black");
		layout.setWidthFull();
		layout.add(new Label(item.getText()));
		layout.setPadding(true);
		layout.setSpacing(false);
		layout.setMargin(false);

		PollCardLikeComponent likeComponent = new PollCardLikeComponent(view, view.getId().get(), card.getId(),
				item.getId());
		likeComponent.setWidthFull();
		layout.add(likeComponent);

		return layout;
	}

	private void addStyles(CardComponent root, VerticalLayout rootLayout) {
		rootLayout.setWidthFull();
		rootLayout.setSpacing(true);
		rootLayout.setMargin(false);
		rootLayout.setPadding(false);
		root.add(rootLayout);

		root.setWidthFull();
		root.getStyle().set("box-shadow", "0.5px solid black");
		root.getStyle().set("border-radius", "1em");
		root.getStyle().set("border", "1px solid var(--material-disabled-text-color)");
		root.addClassName("card-hover");
	}

	@Override
	public void reload() {
		card = cardRepository.findById(root.id()).get();
		reload(card);
	}

	@Override
	public void reload(ScrumCard data) {
		changeText(data.getText());
		changeButtonCommentsCaption(data.getComments());
		getComponentsByType(itemLayout, VerticalLayout.class).stream()
				.map(e -> ((PollCardLikeComponent) e.getComponentAt(1))).forEach(PollCardLikeComponent::reload);
	}

	@Override
	public void changeText(String text) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeButtonCommentsCaption(List<ScrumCardComment> set) {
		// TODO Auto-generated method stub
		
	}

}

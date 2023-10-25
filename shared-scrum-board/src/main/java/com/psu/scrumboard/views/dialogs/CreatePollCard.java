package com.psu.scrumboard.views.dialogs;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.util.Strings;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.enums.ScrumCardType;
import com.psu.scrumboard.model.ScrumPollData;
import com.psu.scrumboard.model.ScrumPollItem;
import com.psu.scrumboard.session.SessionUtils;
import com.psu.scrumboard.stream.ScrumCardCommentStream;
import com.psu.scrumboard.stream.ScrumColumnStream;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.cardandcolumn.ColumnComponent;
import com.psu.scrumboard.views.components.interfaces.BroadcastRegistryInterface;
import com.psu.scrumboard.views.components.interfaces.ComponentInterface;
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

import lombok.extern.log4j.Log4j2;

@Log4j2
public class CreatePollCard extends Dialog implements BroadcastRegistryInterface, ComponentInterface {

	private static final long serialVersionUID = -2119496244059224808L;

	private VerticalLayout root;
	private VerticalLayout pollItemLayout;
	private ScrumView view;
	private ColumnComponent column;
	private String columnId;
	private String cardTitle;
	private Label title;

	public CreatePollCard(ScrumView view, ColumnComponent column, String columnId, String text) {
		this.columnId = columnId;
		this.view = view;
		this.column = column;

		String caption;
		List<String> items = Lists.newArrayList();
		try {
			Pattern p1 = Pattern.compile("(.*)^.*");
			Matcher m1 = p1.matcher(text);
			caption = text;
			while (m1.find()) {
				caption = m1.group();
			}

			Pattern p2 = Pattern.compile("\n(-.+)+");
			Matcher m2 = p2.matcher(text);
			while (m2.find()) {
				String group = m2.group(1);
				items.add(group);
			}
		} catch (Exception e) {
			log.error("Error while parsing poll input", e);
			caption = text;
		}

		setId(columnId);

		root = new VerticalLayout();
		root.setSizeFull();
		add(root);

		HorizontalLayout header = new HorizontalLayout();
		header.setPadding(true);
		header.setSpacing(false);
		header.setMargin(false);
		header.setAlignItems(Alignment.CENTER);
		header.getStyle().set("border", "1px solid black");
		header.getStyle().set("padding-left", "12px");
		header.setWidthFull();
		root.add(header);

		title = new Label();
		title.getStyle().set("font-size", "x-large");
		title.setWidthFull();
		header.add(title);
		Icon plus = VaadinIcon.PLUS.create();
		plus.getStyle().set("color", "#008CBA");
		Button btn = new Button(plus);
		ToolTip.add(btn, "Add Question");
		btn.addClickListener(e -> {
			new ScrumTextArea("Add Poll Option", Strings.EMPTY, savedText -> {
				addpoll(savedText);
			}).open();
		});
		Icon check = VaadinIcon.CHECK.create();
		check.getStyle().set("color", "green");
		Button btnAdd = new Button(check);
		ToolTip.add(btnAdd, "Create Poll");
		btnAdd.addClickListener(e -> {
			ScrumCard card = ScrumCard.builder().type(ScrumCardType.PollCard).ownerId(SessionUtils.getSessionId())
					.text(new Gson()
							.toJson(ScrumPollData.builder().text(title.getText()).items(getpollItems()).build()))
					.build();

			column.addCardAndSave(card);
			ScrumColumnStream.broadcastAddColumn(columnId, card.getId());
			close();
		});

		HorizontalLayout btnLayout = new HorizontalLayout();
		btnLayout.setJustifyContentMode(JustifyContentMode.END);
		btnLayout.add(btn);
		btnLayout.add(btnAdd);

		header.add(btnLayout);

		setWidth("500px");
		setHeight("400px");

		pollItemLayout = new VerticalLayout();
		pollItemLayout.setWidthFull();
		pollItemLayout.setMargin(false);
		pollItemLayout.setPadding(false);
		root.add(pollItemLayout);

		addTitle(caption);

		items.stream().forEachOrdered(e -> {
			addpoll(e);
		});
	}

	private void addTitle(String cardText) {
		title.setText(cardText);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();
		registerBroadcast("card", ScrumCardCommentStream.register(getId().get(), event -> {
			ui.access(() -> {
				if (ScrumConfig.DEBUG) {
					Notification.show("receiving broadcast for update", ScrumConfig.NOTIF_TIME, Position.BOTTOM_END);
				}

				reload();
			});
		}));
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		unRegisterBroadcasters();
	}

	public List<ScrumPollItem> getpollItems() {
		return getComponentsByType(pollItemLayout, HorizontalLayout.class).stream()
				.map(e -> ScrumPollItem.builder().text(((Label) (e.getComponentAt(1))).getText()).build())
				.collect(Collectors.toList());
	}

	private void addpoll(String poll) {
		HorizontalLayout layout = new HorizontalLayout();
		layout.getStyle().set("border", "1.0px dashed black");
		layout.setWidthFull();
		layout.setPadding(true);
		Label title = new Label("- ");
		Label l = new Label(poll);
		layout.add(title, l);
		pollItemLayout.addComponentAsFirst(layout);
	}

	public void reload() {

	}

}

package com.psu.scrumboard.views;

import static com.vaadin.flow.component.notification.Notification.Position.BOTTOM_CENTER;
import static com.vaadin.flow.component.notification.Notification.show;

import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.psu.scrumboard.data.repository.ScrumBoardRepository;
import com.psu.scrumboard.data.repository.ScrumUserRepository;
import com.psu.scrumboard.data.table.ScrumBoard;
import com.psu.scrumboard.data.table.ScrumBoardOptions;
import com.psu.scrumboard.data.table.ScrumBoardUser;
import com.psu.scrumboard.data.table.ScrumColumn;
import com.psu.scrumboard.model.ScrumBoardCurrentUser;
import com.psu.scrumboard.session.SessionUtils;
import com.psu.scrumboard.stream.ScrumBoardStream;
import com.psu.scrumboard.stream.ScrumUsersStream;
import com.psu.scrumboard.utils.Utils;
import com.psu.scrumboard.views.components.ToolTip;
import com.psu.scrumboard.views.components.cardandcolumn.ColumnComponent;
import com.psu.scrumboard.views.components.interfaces.BroadcastRegistryInterface;
import com.psu.scrumboard.views.dialogs.CreateColumn;
import com.psu.scrumboard.views.dialogs.ScrumBoardConfirm;
import com.psu.scrumboard.views.dialogs.ShareLayout;
import com.psu.scrumboard.views.layouts.MainLayout;
import com.psu.scrumboard.views.utils.ScrumBoardViewUtils;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.server.VaadinSession;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The main view is a top-level placeholder for other views.
 */
@Log4j2
@Route(value = ScrumView.NAME, layout = MainLayout.class)
@CssImport(value = "./styles/custom-menu-bar.css", themeFor = "vaadin-menu-bar")
@CssImport(value = "./styles/custom-menu-bar-button.css", themeFor = "vaadin-menu-bar-button")
public class ScrumView extends Div implements HasUrlParameter<String>, BroadcastRegistryInterface {

	public static final String NAME = "scrumboard";

	private static final long serialVersionUID = 8874200985319706829L;
	@Getter
	public HorizontalLayout columns;
	@Autowired
	private ScrumBoardRepository repository;
	@Autowired
	private ScrumUserRepository userRepository;
	@Getter
	private VerticalLayout root;
	@Getter
	private HorizontalLayout header;
	@Getter
	private HorizontalLayout titleHeader;
	@Getter
	private ScrumBoardOptions options;
	@Getter
	private String ownerId;

	@Getter
	private String dataUserId;

	private Button btnUsers;

	private void init() {
		log.info("init");
		setSizeFull();

		root = ScrumBoardViewUtils.createRootLayout();
		add(root);

		titleHeader = createTitleLayout();
		header = createHeaderLayout();
		root.add(titleHeader);
		root.add(header);

		columns = ScrumBoardViewUtils.createColumnLayout();
		root.add(columns);
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		setId(parameter);

		if (!repository.findById(getId().get()).isPresent()) {
			createBoardNotFound(this);
			return;
		}

		ScrumBoard tmp = repository.findById(getId().get()).get();
		if (options == null) {
			options = tmp.getOptions();
			if (options == null) {
				options = ScrumBoardOptions.builder().build();
			}

			ScrumBoardUser u = userRepository.findByDataIdFetched(getId().get());
			if (u == null) {
				u = ScrumBoardUser.builder().build();
			}
			dataUserId = u.getId();
			ownerId = tmp.getOwnerId();
		}

		userRepository.insertUserById(dataUserId,
				ScrumBoardCurrentUser.builder().id(SessionUtils.getSessionId()).active(true).build());

		init();
		reload();
	}

	private void createBoardNotFound(HasComponents rootLayout) {
		log.info("could not find any board with ID: {}", getId().get());
		Button b = new Button("No Session found with ID '" + getId().get() + "'");
		b.addClickListener(e -> UI.getCurrent().navigate(MainView.class));
		setSizeFull();
		VerticalLayout layout = new VerticalLayout();
		layout.setJustifyContentMode(JustifyContentMode.CENTER);
		layout.setSizeFull();
		rootLayout.add(layout);
		layout.add(b);
		layout.setAlignSelf(Alignment.CENTER, b);
	}

	@Override
	protected void onAttach(AttachEvent attachEvent) {
		UI ui = UI.getCurrent();

		registerBroadcast("board", ScrumBoardStream.register(getId().get(), event -> {
			ui.access(() -> {
				if (ScrumConfig.DEBUG) {
					Notification.show("receiving broadcast for update", ScrumConfig.NOTIF_TIME, Position.BOTTOM_END);
				}
				reload();
			});
		}));

		registerBroadcast("users", ScrumUsersStream.register(getId().get(), event -> {
			ui.access(() -> {
				if (ScrumConfig.DEBUG) {
					Notification.show("receiving broadcast for update", ScrumConfig.NOTIF_TIME, Position.BOTTOM_END);
				}

				changeUsersCounter();
			});
		}));
		ScrumUsersStream.broadcast(getId().get(), "update");
		super.onAttach(attachEvent);
	}

	@Override
	protected void onDetach(DetachEvent detachEvent) {
		log.info("detaching ui");
		userRepository.deleteUserById(dataUserId, SessionUtils.getSessionId());
		ScrumUsersStream.broadcast(getId().get(), "delete");

		unRegisterBroadcasters();

		super.onDetach(detachEvent);
	}

	public void reload() {
		log.info("sync & refreshing data: {}", getId().get());
		ScrumBoard tmp = repository.findByIdFetched(getId().get());

		// update layout with new missing data
		tmp.getColumns().stream().sorted(Comparator.comparing(ScrumBoardPosition::getPosition)).forEachOrdered(pdc -> {
			ColumnComponent column = getColumnLayoutById(pdc.getId());
			if (column == null) {
				// add card as new card
				column = addColumnLayout(pdc);
			}

			column.reload();
		});

		// removes deleted columns
		getColumnComponents().stream()
				.filter(e -> tmp.getColumns().stream().noneMatch(x -> x.getId().equals(e.getId().get())))
				.collect(Collectors.toList()).forEach(columns::remove);
	}

	public List<ColumnComponent> getColumnComponents() {
		List<ColumnComponent> components = Lists.newArrayList();
		for (int i = 0; i < columns.getComponentCount(); i++) {
			if (columns.getComponentAt(i) instanceof ColumnComponent) {
				components.add((ColumnComponent) columns.getComponentAt(i));
			}
		}

		return components;
	}

	public void addColumn(String id, String ownerId, String name, String description) {
		log.info("add column: {} ({})", name, id);
		ScrumBoard tmp = repository.findByIdFetched(getId().get());

		tmp.addColumn(ScrumColumn.builder().id(id).name(name).description(description).ownerId(ownerId)
				.position(ScrumBoardViewUtils.calculateNextPosition(tmp.getColumns())).build());

		repository.save(tmp);
	}

	public ColumnComponent addColumnLayout(ScrumColumn column) {
		if (getColumnLayoutById(column.getId()) != null) {
			log.warn("column already exists: {} - {}", column.getId(), column.getName());
			return null;
		}

		ColumnComponent col = new ColumnComponent(this, column);
		columns.add(col);
		return col;
	}

	public ColumnComponent getColumnLayoutById(String columnId) {
		for (int i = 0; i < columns.getComponentCount(); i++) {
			ColumnComponent col = (ColumnComponent) columns.getComponentAt(i);
			if (col.getId().get().equals(columnId)) {
				return col;
			}
		}

		return null;
	}

	public HorizontalLayout createTitleLayout() {
		HorizontalLayout left = new HorizontalLayout();
		left.setAlignItems(Alignment.CENTER);
		Label label = new Label("Shared Scrum Board");
		label.getStyle().set("font-size", "24px").set("color", "#008CBA").set("font-weight", "bold").set("text-transform",
				"uppercase");
		left.add(label);
		left.setJustifyContentMode(JustifyContentMode.CENTER);
		left.setSpacing(false);
		left.setMargin(false);
		left.setWidthFull();
		return left;
	}

	public HorizontalLayout createHeaderLayout() {
		HorizontalLayout left = new HorizontalLayout();
		left.setAlignItems(Alignment.CENTER);
		left.setSpacing(false);
		left.setMargin(false);
		left.setWidthFull();

		HorizontalLayout right = new HorizontalLayout();
		right.setJustifyContentMode(JustifyContentMode.END);
		right.setAlignItems(Alignment.CENTER);
		right.setSpacing(false);
		right.setMargin(false);
		right.setWidthFull();

		HorizontalLayout layout = new HorizontalLayout();
		layout.getStyle().set("background-color", "#008CBA"); // Set the background color
		layout.getStyle().set("border-radius", "5px"); // Add border radius
		layout.getStyle().set("font-size", "16px"); // Set the font size
		layout.setAlignItems(Alignment.CENTER);
		layout.setSpacing(false);
		layout.setMargin(false);
		layout.setWidthFull();
		layout.add(right, left);

		Button b = new Button("Add Column", VaadinIcon.LIST.create());
		b.getStyle().set("color", "white"); // Set the text color
		ToolTip.add(b, "Create new column");

		b.addClickListener(e -> {
			if (options.getMaxColumns() > 0) {
				if (columns.getComponentCount() >= options.getMaxColumns()) {
					Notification.show("Column limit reached", ScrumConfig.NOTIF_TIME, Position.MIDDLE);
					return;
				}
			}

			new CreateColumn(Strings.EMPTY, Strings.EMPTY, saveListener -> {
				String name = saveListener[0];
				String description = saveListener[1];

				if (name.isEmpty()) {
					show("Please enter a column name", 3000, BOTTOM_CENTER);
					return;
				}

				addColumn(Utils.randomId(), SessionUtils.getSessionId(), name, description);

				ScrumBoardStream.broadcast(getId().get(), "update");
			}).open();
		});
		left.add(b);
		
		Button btn = new Button("Logout", VaadinIcon.SIGN_OUT.create());
		btn.getStyle().set("color", "white"); // Set the text color
		btn.addClickListener(e ->{
			VaadinSession.getCurrent().getSession().invalidate();
			UI.getCurrent().getSession().close();	
			UI.getCurrent().navigate(MainView.class);
		});
		
		left.add(btn);
		if (ScrumConfig.DEBUG) {
			Button btnDebug = new Button("Debug", VaadinIcon.BUG.create());
			btnDebug.addClickListener(e -> {
				Dialog d = new Dialog();
				d.setWidth("500px");
				d.setHeight("500px");
				Label t = new Label(new GsonBuilder().setPrettyPrinting().create()
						.toJson(repository.findByIdFetched(getId().get())));
				t.getStyle().set("white-space", "pre-wrap");
				t.setSizeFull();
				d.add(t);
				d.open();
			});
			left.add(btnDebug);
		}

		btnUsers = new Button(VaadinIcon.GROUP.create());
		btnUsers.getStyle().set("color", "white"); // Set the text color
		right.add(btnUsers);

		MenuBar menuBar = new MenuBar();
		ToolTip.add(menuBar, "Settings");

		menuBar.getStyle().set("margin-right", "5px");
		menuBar.getStyle().set("margin-left", "1px");
		menuBar.addThemeName("no-overflow-button");

		right.add(menuBar);
		Icon icon = VaadinIcon.COG.create();
		icon.getStyle().set("color", "white");
		MenuItem menuItem = menuBar.addItem(icon);
		if (ScrumBoardViewUtils.isAllowed(ownerId, ownerId)) {
			menuItem.getSubMenu().addItem("Delete Board", e -> {
				ScrumBoardConfirm
						.createQuestion().withCaption("Delete Board").withMessage(String
								.format("This will delete the board and '%s' columns", columns.getComponentCount()))
						.withOkButton(() -> {
							repository.deleteById(getId().get());
							UI.getCurrent().navigate(MainView.class);
						}).withCancelButton().open();
			});
		}

		menuItem.getSubMenu().addItem("Reset all given Likes", e -> {
			ScrumBoardConfirm.createQuestion().withCaption("Reset all given Likes")
					.withMessage("This will delete every like on any card").withOkButton(() -> {
						ScrumBoard data = repository.findByIdFetched(getId().get());
						data.resetLikes();
						repository.save(data);
						ScrumBoardStream.broadcast(getId().get(), "update");
					}).withCancelButton().open();
		});

		menuItem.getSubMenu().addItem("Refresh", e -> reload());

		menuItem.getSubMenu().addItem("Share with others", e -> {
			new ShareLayout("Share Layout", () -> getId().get(),
					() -> createCurrentUrl(VaadinService.getCurrentRequest())).open();
		});
		return layout;
	}

	private String createCurrentUrl(VaadinRequest request) {
		try {
			VaadinServletRequest req = (VaadinServletRequest) request; // VaadinService.getCurrentRequest();
			StringBuffer uriString = req.getRequestURL();
			URI uri = new URI(uriString.toString());

			String url = uri.toString();
			if (!url.endsWith(getId().get())) {
				url = uri.toString() + NAME + "/" + getId().get();
			}

			return url;
		} catch (Exception e) {
			log.error("failed to create url share resource", e);
		}

		return "Error creating URL Resource";
	}

	public void changeUsersCounter() {
		getId().ifPresent(e -> {
			if (btnUsers != null) {
				btnUsers.setText(String.valueOf(userRepository.countByDataId(e)));
			}
		});
	}

}

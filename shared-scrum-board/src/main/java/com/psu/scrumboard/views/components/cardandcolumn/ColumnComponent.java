package com.psu.scrumboard.views.components.cardandcolumn;

import static java.lang.String.format;

import com.psu.scrumboard.views.components.ToolTip;
import com.flowingcode.vaadin.addons.fontawesome.FontAwesome;
import com.google.common.collect.Sets;
import com.psu.scrumboard.config.ScrumConfig;
import com.psu.scrumboard.data.interfaces.ScrumBoardPosition;
import com.psu.scrumboard.data.repository.ScrumBoardRepository;
import com.psu.scrumboard.data.repository.ScrumCardRepository;
import com.psu.scrumboard.data.repository.ScrumColumnRepository;
import com.psu.scrumboard.data.table.ScrumBoard;
import com.psu.scrumboard.data.table.ScrumCard;
import com.psu.scrumboard.data.table.ScrumColumn;
import com.psu.scrumboard.enums.ScrumCardType;
import com.psu.scrumboard.model.ScrumTextItem;
import com.psu.scrumboard.session.SessionUtils;
import com.psu.scrumboard.stream.ScrumBoardStream;
import com.psu.scrumboard.stream.ScrumColumnStream;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.utils.Utils;
import com.psu.scrumboard.views.ScrumView;
import com.psu.scrumboard.views.components.interfaces.BroadcastRegistryInterface;
import com.psu.scrumboard.views.components.interfaces.ComponentInterface;
import com.psu.scrumboard.views.dialogs.CreateColumn;
import com.psu.scrumboard.views.dialogs.CreatePollCard;
import com.psu.scrumboard.views.dialogs.ScrumBoardConfirm;
import com.psu.scrumboard.views.utils.ScrumBoardViewUtils;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dnd.DropEffect;
import com.vaadin.flow.component.dnd.DropTarget;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.value.ValueChangeMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

@Log4j2
public class ColumnComponent extends VerticalLayout implements BroadcastRegistryInterface, ComponentInterface {

  private static final long serialVersionUID = 8415434953831247614L;
  private final HorizontalLayout captionLayout;
  private Icon icon;

  private final ScrumBoardRepository dataRepository = SpringContext.getBean(ScrumBoardRepository.class);
  private final ScrumCardRepository cardRepository = SpringContext.getBean(ScrumCardRepository.class);
  private final ScrumColumnRepository repository = SpringContext.getBean(ScrumColumnRepository.class);

  @Getter
  private final ScrumView view;

  private TextArea area;
  private H3 h3;
  private VerticalLayout cards;
  private String id;
  private String ownerid;

  public ColumnComponent(ScrumView view, ScrumColumn column) {
    this.view = view;
    setId(column.getId());
    this.id = getId().get();
    this.ownerid=column.getOwnerId();
    setWidth("400px");
    getStyle().set("box-shadow", "var(--material-shadow-elevation-4dp)");
    setSpacing(true);
    setMargin(false);

    h3 = new H3();
    h3.setWidthFull();
    h3.getStyle()
      .set("margin", "unset")
      .set("padding-left", "0.2em");

    icon = VaadinIcon.INFO_CIRCLE_O.create();
	icon.getStyle().set("color", "#008CBA");
    captionLayout = new HorizontalLayout(icon, h3);

    // initialized in changeTitle
    changeTitle(column.getName(), column.getDescription(), column.getPosition());

    captionLayout.setAlignItems(Alignment.CENTER);
    captionLayout.setMargin(false);
    captionLayout.setSpacing(true);

    addTitleOptions(view, column, captionLayout);

    captionLayout.setWidthFull();
    captionLayout.setVerticalComponentAlignment(Alignment.CENTER, h3);

    add(captionLayout);

    VerticalLayout layoutHeader = new VerticalLayout();
    layoutHeader.getStyle()
                .set("flex-shrink", "0");
    layoutHeader.getStyle()
                .set("border", "2px solid black");
    layoutHeader.getStyle()
                .set("overflow", "auto");
    layoutHeader.setWidthFull();
    layoutHeader.setHeight("200px");
    add(layoutHeader);

    area = new TextArea();
    area.setWidthFull();
    area.getStyle()
        .set("flex-grow", "1");
    area.setPlaceholder("Enter your text.");

    if (view.getOptions()
            .getMaxCardTextLength() > 0) {
      area.setMaxLength(view.getOptions()
                            .getMaxCardTextLength());
      area.setValueChangeMode(ValueChangeMode.EAGER);
      area.addValueChangeListener(e -> {
        if (e.getSource()
             .getValue()
             .length() > view.getOptions()
                             .getMaxCardTextLength()) {
          layoutHeader.getStyle()
                      .set("border-color", "f44336");
        } else {
          layoutHeader.getStyle()
                      .remove("border-color");
        }
      });
    }

    VerticalLayout txtLayout = new VerticalLayout(area);
    txtLayout.setWidthFull();
    txtLayout.setPadding(false);
    txtLayout.getStyle()
             .set("overflow-y", "auto");
    txtLayout.getStyle()
             .set("flex-grow", "1");

    layoutHeader.add(txtLayout);
    
    Icon cardi=VaadinIcon.PLUS.create();
	cardi.getStyle().set("color", "#008CBA");
    Button btnAdd = new Button("Card", cardi);
	btnAdd.getStyle().set("color", "#008CBA");
    ToolTip.add(btnAdd, "Add a Card");
    btnAdd.setWidthFull();
    btnAdd.addClickListener(e -> {
      if (view.getOptions()
              .getMaxCards() > 0) {
        if (cards.getComponentCount() >= view.getOptions()
                                             .getMaxCards()) {
          Notification.show("Card limit reached", ScrumConfig.NOTIF_TIME, Position.MIDDLE);
          return;
        }
      }

      if (StringUtils.isBlank(area.getValue())) {
        Notification.show("Please enter a text", ScrumConfig.NOTIF_TIME, Position.MIDDLE);
        return;
      }

      ScrumCard card = ScrumCard.builder()
                            .type(ScrumCardType.TextComponentCard)
                            .ownerId(SessionUtils.getSessionId())
                            .build();
      card.setTextByType(ScrumTextItem.builder()
                                 .text(area.getValue())
                                 .build());
      ScrumColumn col = addCardAndSave(card);
      update(col.getId());

      area.clear();
      area.focus();
    });
    Icon trash=VaadinIcon.TRASH.create();
	trash.getStyle().set("color", "#008CBA");
    Button btnCancel = new Button("Clear", trash);
	btnCancel.getStyle().set("color", "#008CBA");
    ToolTip.add(btnCancel, "Clear the Input");
    btnCancel.setWidthFull();
    btnCancel.addClickListener(e -> {
      area.clear();
    });
    Icon heart=VaadinIcon.HEART.create();
	heart.getStyle().set("color", "#008CBA");
    Button btnVote = new Button("Poll", heart);
	btnVote.getStyle().set("color", "#008CBA");
    ToolTip.add(btnVote, "Do Vote");
    btnVote.setWidthFull();
    btnVote.addClickListener(e -> {
        if (view.getOptions()
                .getMaxCards() > 0) {
          if (cards.getComponentCount() >= view.getOptions()
                                               .getMaxCards()) {
            Notification.show("Card limit reached",
                              ScrumConfig.NOTIF_TIME,
                              Position.MIDDLE);
            return;
          }
        }

        if (StringUtils.isBlank(area.getValue())) {
          Notification.show("Please enter a text",
          		ScrumConfig.NOTIF_TIME,
                            Position.MIDDLE);
          return;
        }

        new CreatePollCard(view, this, getId().get(), area.getValue()).open();
        area.clear();
      });

    HorizontalLayout btnLayout = new HorizontalLayout(btnCancel, btnAdd, btnVote);
    btnLayout.setWidthFull();

    layoutHeader.add(btnLayout);
    setHorizontalComponentAlignment(FlexComponent.Alignment.CENTER, h3);

    cards = new VerticalLayout();
    cards.getStyle()
         .set("overflow", "auto");
    cards.setMargin(false);
    cards.setPadding(false);
    cards.setSpacing(true);
    cards.setHeightFull();

    DropTarget<VerticalLayout> dropTarget = DropTarget.create(cards);
    dropTarget.setDropEffect(DropEffect.MOVE);
    dropTarget.addDropListener(e -> {
      e.getDragSourceComponent()
       .ifPresent(card -> {
         String dragColumnId = e.getDragData()
                                .get()
                                .toString();
         if (hasCardById(dragColumnId)) {
           log.debug("dropping in same layout is not supported");
           return;
         }

         CardComponent droppedCard = (CardComponent) card;

         // update
         log.debug("receive dropped card: " + droppedCard.getId()
                                                         .get());
         droppedCard.getCard()
                    .setId(Utils.randomId());

         ScrumColumn col = addCardAndSave(droppedCard.getCard());
         update(col.getId());

         // delete old and update
         droppedCard.deleteCard();
       });
    });

    add(cards);
  }

  private void addTitleOptions(ScrumView view, ScrumColumn column, HorizontalLayout captionLayout) {
    MenuBar menuBar = new MenuBar();
    ToolTip.add(menuBar, "Settings");
    menuBar.getStyle()
           .set("margin-right", "1px");
    menuBar.getStyle()
           .set("margin-left", "1px");
    menuBar.getStyle().set("color", "#008CBA");
    menuBar.addThemeName("no-overflow-button");

    captionLayout.add(menuBar);

    MenuItem menuItem = menuBar.addItem(FontAwesome.Solid.ELLIPSIS_V.create());
    if (ScrumBoardViewUtils.isAllowed(view.getOwnerId(), column.getOwnerId())) {
      menuItem.getSubMenu()
              .addItem("Edit", e -> {
                new CreateColumn(h3.getText(),
                                       ToolTip.getTooltip(icon, Strings.EMPTY),
                                       saveListener -> {
                                         log.info("Edit column: " + getId().get());
                                         ScrumColumn c = repository.findById(id)
                                                                 .get();

                                         c.setName(saveListener[0]);
                                         c.setDescription(saveListener[1]);
                                         repository.save(c);
                                         ScrumColumnStream.broadcast(id, "update");
                                       }).open();
              });

      menuItem.getSubMenu()
              .addItem("Shuffle Cards", e -> {
                ScrumColumn tmp = repository.findById(getId().get())
                                          .get();

                List<ScrumCard> toShuffle = tmp
                    .getCards()
                    .stream()
                    .collect(Collectors.toList());

                Collections.shuffle(toShuffle);

                // fix order
                IntStream.range(0, toShuffle.size())
                         .forEachOrdered(counter -> {
                           ScrumCard cc = toShuffle.get(counter);
                           cc.setGetScrumPosition(counter);
                         });

                tmp.setCards(Sets.newHashSet(toShuffle));
                tmp = repository.save(tmp);
                ScrumColumnStream.broadcast(getId().get(), ScrumColumnStream.MESSAGE_SORT);
              });

      menuItem.getSubMenu()
              .addItem("Sort Cards by Votes", e -> {
                ScrumColumn tmp = repository.findById(getId().get())
                                          .get();

                List<ScrumCard> sortedByVotes = tmp
                    .getCards()
                    .stream()
                    .sorted(Comparator.comparingLong(ScrumCard::getLikes))
                    .collect(Collectors.toList());

                // fix order
                IntStream.range(0, sortedByVotes.size())
                         .forEachOrdered(counter -> {
                           ScrumCard cc = sortedByVotes.get(counter);
                           cc.setGetScrumPosition(counter);
                         });

                tmp.setCards(Sets.newHashSet(sortedByVotes));
                tmp = repository.save(tmp);
                ScrumColumnStream.broadcast(getId().get(), ScrumColumnStream.MESSAGE_SORT);
              });

      menuItem.getSubMenu()
              .addItem("Delete", e -> {
                ScrumBoardConfirm.createQuestion()
                               .withCaption("Deleting Column: " + column.getName())
                               .withMessage(format("This will remove '%s' cards",
                                                   cards.getComponentCount()))
                               .withOkButton(this::deleteColumn)
                               .withCancelButton()
                               .open();
              });
    }

  }

  private void update(String columnId) {
    ScrumColumnStream.broadcast(getId().get(), ScrumColumnStream.ADD_COLUMN + columnId);
  }

  private void deleteColumn() {
    log.info("delete column: " + getId().get());
    ScrumBoard c = dataRepository.findByIdFetched(view.getId()
                                                   .get());
    if(SessionUtils.getSessionId().equals(ownerid)) {
    	c.removeColumnById(id);
    	Notification.show("Deleting Column: " + h3.getText(),
        		ScrumConfig.NOTIF_TIME,
                          Position.MIDDLE);
    }
    else {
    	Notification.show("You are not allowed to delete the column " + h3.getText(),
        		ScrumConfig.NOTIF_TIME,
                          Position.MIDDLE);
    }
    dataRepository.save(c);
    ScrumBoardStream.broadcast(view.getId()
                                   .get(), "update");
  }

  public ScrumColumn addCardAndSave(ScrumCard card) {
    ScrumColumn tmp = repository.findById(getId().get())
                              .get();
    card.setGetScrumPosition(ScrumBoardViewUtils.calculateNextPosition(tmp.getCards()));
    tmp.addCard(card);
    repository.save(tmp);
    log.info("add card: {}", card.getId());
    return tmp;
  }

  private CardComponent addCardLayout(ScrumCard card) {
    CardComponent cc = new CardComponent(view, this, getId().get(), card);

    if (view.getOptions()
            .isCardSortDirectionDesc()) {
      cards.addComponentAsFirst(cc);
    } else {
      cards.add(cc);
    }
    return cc;
  }

  public void reload() {
    log.info("reloading column: {}", getId().get());
    ScrumColumn data = repository.findById(getId().get())
                               .get();

    changeTitle(data.getName(), data.getDescription(), data.getPosition());

    // update layout with new missing data
    data.getCards()
        .stream()
        .sorted(Comparator.comparing(ScrumBoardPosition::getPosition))
        .forEachOrdered(pdc -> {
          CardComponent card = getComponentById(cards, CardComponent.class, pdc.getId());
          if (card == null) {
            // add card as new card
            card = addCardLayout(pdc);
          }

          card.reload();
        });

    // remove old
    getComponentsByType(cards, Component.class)
        .stream()
        .filter(e -> data.getCards()
                         .stream()
                         .noneMatch(x -> x.getId()
                                          .equals(e.getId()
                                                   .get())))
        .collect(Collectors.toList())
        .forEach(e -> {
          cards.remove(e);
        });
  }

  public void changeTitle(String string, String description, int order) {
    if (!h3.getText()
           .equals(string)) {
      h3.setText(string);
      h3.getStyle().set("color", "#008CBA");
    }

    // change only at differences
    String lastTooltip = com.psu.scrumboard.views.components.ToolTip.getTooltip(icon, Strings.EMPTY);
    if (!lastTooltip.equals(description)) {
      log.debug("set column tooltip from/to: {} -> {}", lastTooltip, description);
      Icon iconTmp = null;
      if (StringUtils.isBlank(description)) {
    		
        iconTmp = VaadinIcon.INFO_CIRCLE_O.create();
        iconTmp.getStyle().set("color", "#008CBA");
      } else {
    	  
        iconTmp = VaadinIcon.INFO_CIRCLE.create();
        iconTmp.getStyle().set("color", "#008CBA");
      }

      captionLayout.replace(icon, iconTmp);
      icon = iconTmp;

      ToolTip.add(icon, description);
    }

    if (ScrumConfig.DEBUG) {
      h3.setText(string + " (" + order + ")");
      h3.getStyle().set("color", "#008CBA");
    }
  }

  public boolean hasCardById(String id) {
    return cards.getChildren()
                .anyMatch(e -> e.getId()
                                .get()
                                .equals(id));
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    UI ui = UI.getCurrent();
    registerBroadcast("column", ScrumColumnStream.register(getId().get(), event -> {
      ui.access(() -> {
        if (ScrumConfig.DEBUG) {
          Notification.show("receiving broadcast for update", ScrumConfig.NOTIF_TIME,
                            Position.BOTTOM_END);
        }

        String[] cmd = event.split("\\.");

        switch (cmd[0]) {
          case ScrumColumnStream.MESSAGE_SORT:
            cards.removeAll();
            reload();
            break;

          case ScrumColumnStream.ADD_COLUMN:
            ScrumCard pdc = cardRepository.findById(cmd[1])
                                        .get();
            CardComponent card = getComponentById(cards, CardComponent.class, pdc.getId());
            if (card == null) {
              card = addCardLayout(pdc);
            }

            card.reload();
            break;

          default:
            reload();
            break;
        }

      });
    }));
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    unRegisterBroadcasters();
  }

}

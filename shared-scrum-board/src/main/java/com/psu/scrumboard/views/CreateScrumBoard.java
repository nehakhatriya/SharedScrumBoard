package com.psu.scrumboard.views;
import com.psu.scrumboard.data.repository.ScrumBoardRepository;
import com.psu.scrumboard.data.table.ScrumBoard;
import com.psu.scrumboard.data.table.ScrumBoardOptions;
import com.psu.scrumboard.session.SessionUtils;
import com.psu.scrumboard.utils.SpringContext;
import com.psu.scrumboard.views.components.CustomNumberField;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import org.springframework.beans.factory.annotation.Autowired;

public class CreateScrumBoard extends VerticalLayout{

  private static final long serialVersionUID = 2274992601002314827L;

  @Autowired
  private ScrumBoardRepository repository = SpringContext.getBean(ScrumBoardRepository.class);

  private NumberField nmbColumnsMax;
  private NumberField nmbCardsMax;
  private NumberField nmbCardTextLengthMax;
  private int nmbCardLikesMaxPerUser = 1;
  private Checkbox chkOptLatestCardOnTop;

  public CreateScrumBoard() {
    setWidth("450px");
    setHeight("700px");

    VerticalLayout layout = new VerticalLayout();
    layout.setWidthFull();
    layout.setFlexGrow(1);
    add(layout);

    layout.add(createTitle());
    layout.add(createOptionsLayout());
    layout.add(createBottomLayout());

  }

  private H3 createTitle() {
    H3 title = new H3("Create New Board?");
    title.getStyle()
         .set("text-align", "center");
    title.setWidthFull();
    return title;
  }

  private VerticalLayout createOptionsLayout() {
    VerticalLayout layout = new VerticalLayout();
    layout.setWidthFull();
    layout.setFlexGrow(1);
    add(layout);

    nmbColumnsMax = createNumberField(layout, "Max Columns", 0, 0, 50);
    nmbCardsMax = createNumberField(layout, "Max Cards", 0, 0, 99);
    nmbCardTextLengthMax = createNumberField(layout, "Max Card Text Length", 0, 0, 999);
    chkOptLatestCardOnTop = new Checkbox("Would you like oldest card in column on top?");
    chkOptLatestCardOnTop.setValue(false);
    chkOptLatestCardOnTop.setWidthFull();
    layout.add(chkOptLatestCardOnTop);

    return layout;
  }

  private NumberField createNumberField(VerticalLayout layout, String title, int defaultValue,
                                        int min, int max) {
    CustomNumberField numberField = new CustomNumberField(title, min, max, defaultValue, true);
    numberField.setWidthFull();

    layout.add(numberField);

    return numberField;
  }

  private HorizontalLayout createBottomLayout() {
    Button btnOk = new Button("Create");
    btnOk.setWidthFull();
    btnOk.addClickListener(e -> {
      ScrumBoard p = repository.save(
    		  ScrumBoard.builder()
                 .ownerId(SessionUtils.getSessionId())
                 .options(ScrumBoardOptions.builder()
                                    .maxColumns(getCurrentOrDefaultValue(nmbColumnsMax))
                                    .maxCards(getCurrentOrDefaultValue(nmbCardsMax))
                                    .maxCardTextLength(getCurrentOrDefaultValue(nmbCardTextLengthMax))
                                    .maxLikesPerUserPerCard(nmbCardLikesMaxPerUser)
                                    .cardSortDirectionDesc(!chkOptLatestCardOnTop.getValue())
                                    .build())
                 .build());
      
      UI.getCurrent()
        .navigate(ScrumView.class, p.getId());
    });

    HorizontalLayout l = new HorizontalLayout(btnOk);
    l.setWidthFull();
    return l;
  }

  private int getCurrentOrDefaultValue(NumberField field) {
    return field.getValue()
                .intValue() == 0 ? (int) field.getMax() : field.getValue()
                                                               .intValue();
  }

}

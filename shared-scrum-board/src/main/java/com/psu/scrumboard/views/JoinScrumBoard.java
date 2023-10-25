package com.psu.scrumboard.views;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;


public class JoinScrumBoard extends VerticalLayout{

  private static final long serialVersionUID = 2274992601002314827L;

  public JoinScrumBoard() {
    setWidth("450px");
    setHeight("700px");

    VerticalLayout layout = new VerticalLayout();
    layout.setWidthFull();
    layout.setFlexGrow(1);
    add(layout);

    layout.add(createTitle());
    
    layout.add(createJoinLayout());
  }

  private H3 createTitle() {
	    H3 title = new H3("Join an existing board?");
	    title.getStyle()
	         .set("text-align", "center");
	    title.setWidthFull();
	    return title;
	  }

  private VerticalLayout createJoinLayout() {
	 TextField input = new TextField();
	 input.setPlaceholder("Please enter your Session-ID");
	 input.setWidth("350px");	
	 Button btnOk = new Button("Join");
	 btnOk.setWidthFull();
	 btnOk.addClickListener(e -> {
	      UI.getCurrent()
	        .navigate(ScrumView.class, input.getValue());
	    });
	 VerticalLayout hl = new VerticalLayout(input);
	 hl.add(btnOk);
	 return hl;
  }
}

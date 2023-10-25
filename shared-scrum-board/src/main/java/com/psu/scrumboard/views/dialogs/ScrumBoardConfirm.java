package com.psu.scrumboard.views.dialogs;

import org.claspina.confirmdialog.ConfirmDialog;

public class ScrumBoardConfirm extends ConfirmDialog {

	private static final long serialVersionUID = 501671212472064502L;

	public ScrumBoardConfirm() {
		super();
		getChildren().iterator().next().getElement().getStyle().set("padding-left", "30px").set("padding-right", "30px");
	}

    public static ScrumBoardConfirm create() {
        return new ScrumBoardConfirm();
    }

    public static ConfirmDialog createInfo() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getInfoIcon());
    }

    public static ConfirmDialog createQuestion() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getQuestionIcon());
    }

    public static ConfirmDialog createWarning() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getWarningIcon());
    }

    public static ConfirmDialog createError() {
        return create().withIcon(DIALOG_DEFAULT_ICON_FACTORY.getErrorIcon());
    }
	
}

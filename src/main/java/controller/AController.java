package controller;


import view.AView;

public abstract class AController {
    private AView view;

    public AView getView() {
        return view;
    }
}

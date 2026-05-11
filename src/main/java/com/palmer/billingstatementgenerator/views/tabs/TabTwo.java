package com.palmer.billingstatementgenerator.views.tabs;

import com.palmer.billingstatementgenerator.models.StatementContext;
import com.palmer.billingstatementgenerator.views.controllers.BaseFXMLLoader;

public class TabTwo extends GeneratorTabs {

    public TabTwo(String tabTitle, boolean showPrev, boolean showNext, boolean showClear) {
        super(tabTitle, showPrev, showNext, showClear);
    }

    @Override
    protected void addForm() {
        // load the FXML and merge into the GeneratorTabs grid
        BaseFXMLLoader.loadIntoGrid(grid, "/com/palmer/billingstatementgenerator/views/tab_two.fxml");
    }
}

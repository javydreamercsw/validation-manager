package com.validation.manager.core;

import java.util.logging.Logger;

public abstract class AbstractProvider implements IMainContentProvider {

    private VMUI ui;
    protected final Logger LOG
            = Logger.getLogger(getClass().getSimpleName());

    @Override
    public void setUI(VMUI ui) {
        this.ui = ui;
    }

    @Override
    public VMUI getUI() {
        return ui;
    }

    @Override
    public boolean shouldDisplay() {
        return true;
    }

    @Override
    public void update() {
        //Do nothing by default
    }
}

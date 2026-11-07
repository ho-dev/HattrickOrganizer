package core.module;

import core.model.TranslationFacility;

import javax.swing.JMenu;
import javax.swing.JPanel;

public abstract class DefaultModule implements IModule {

    private JPanel modulePanel = null;
	private boolean isStartUp = false;
	private boolean isActive = false;
	public abstract int getModuleId();
	public abstract String getKey();
	public abstract JPanel createTabPanel();

    @Override
    public JPanel getTabPanel() {
        if (modulePanel == null) {
            modulePanel = createTabPanel();
        }
        return modulePanel;
    }

    public String getDescription() {
        return TranslationFacility.tr(getKey());
    }

	protected DefaultModule(){}

	protected DefaultModule(boolean isActive){
		this.isActive = isActive;
	}

	public boolean hasMainTab() {
		return true;
	}

	public boolean hasConfigPanel() {
		return false;
	}

	public JPanel createConfigPanel() {
		return null;
	}

	public boolean isStartup() {
		return isStartUp;
	}

	public void setStartup(boolean value) {
		isStartUp = value;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean value) {
		isActive = value;
	}

	@Override
	public String toString(){
		return getDescription();
	}

	public boolean hasMenu(){
		return false;
	}
	public JMenu getMenu(){
		return null;
	}

	public int getStatus(){
		if(isStartup())
			return STATUS_STARTUP;
		if(isActive())
			return STATUS_ACTIVATED;
		return STATUS_DEACTIVATED;
	}


	public void setStatus(int statusId){
		setStartup(false);
		setActive(false);
		if(statusId > STATUS_DEACTIVATED)
			setActive(true);
		if(statusId > STATUS_ACTIVATED)
			setStartup(true);
	}

	@Override
	public void storeUserSettings(){}
}

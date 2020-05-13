package rotp.model.game;

import java.io.Serializable;

/**
 * Governor options.
 */
public class GovernorOptions implements Serializable {
    // TODO: Add serialVersionUID and merge with GovernorOptions when we can 
    // break save game compatibility
    public enum GatesGovernor {
        None,
        Rich,
        All
    }
    // keep backwards compatibility with system properties
    private boolean governorOnByDefault = !"false".equalsIgnoreCase(System.getProperty("defaultgovernor", "true"));;
    private boolean autotransport = "true".equalsIgnoreCase(System.getProperty("autotransport", "false"));
    private GatesGovernor gates = "false".equalsIgnoreCase(System.getProperty("autogate", "true")) ? GatesGovernor.None : GatesGovernor.Rich;

    public GovernorOptions() {
    }

    public boolean isGovernorOnByDefault() {
        return governorOnByDefault;
    }

    public boolean isAutotransport() {
        return autotransport;
    }

    public GatesGovernor getGates() {
        return gates;
    }

    public void setGovernorOnByDefault(boolean governorOnByDefault) {
        this.governorOnByDefault = governorOnByDefault;
    }

    public void setAutotransport(boolean autotransport) {
        this.autotransport = autotransport;
    }

    public void setGates(GatesGovernor gates) {
        this.gates = gates;
    }
}

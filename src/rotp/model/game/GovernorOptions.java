package rotp.model.game;

import java.io.Serializable;

/**
 * Governor options.
 */
public class GovernorOptions implements Serializable {
    public enum GatesGovernor {
        None,
        Rich,
        All
    }
    // keep backwards compatibility with system properties
    private boolean governorOnByDefault = !"false".equalsIgnoreCase(System.getProperty("defaultgovernor", "true"));;
    private boolean autotransport = "true".equalsIgnoreCase(System.getProperty("autotransport", "false"));
    private GatesGovernor gates = "false".equalsIgnoreCase(System.getProperty("autogate", "true")) ? GatesGovernor.None : GatesGovernor.Rich;

    private int transportPopulation = 10;
    private int transportMaxPercent = 10;
    // 1.5x for destinations inside nebulae
    private int transportMaxTurns = 5;

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

    public int getTransportPopulation() {
        return transportPopulation;
    }

    public void setTransportPopulation(int transportPopulation) {
        this.transportPopulation = transportPopulation;
    }

    public int getTransportMaxPercent() {
        return transportMaxPercent;
    }

    public void setTransportMaxPercent(int transportMaxPercent) {
        this.transportMaxPercent = transportMaxPercent;
    }

    public int getTransportMaxTurns() {
        return transportMaxTurns;
    }

    public void setTransportMaxTurns(int transportMaxTurns) {
        this.transportMaxTurns = transportMaxTurns;
    }
}

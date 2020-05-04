package rotp.model.game;

import java.io.Serializable;

/**
 * Governor options.
 * TODO: Merge with GovernorOptions next time we're OK to break save game compatibility
 */
@Deprecated
public class GovernorOptions2 implements Serializable {
    private static final long serialVersionUID = 1l;

    private int transportPopulation = 10;
    private int transportMaxPercent = 10;
    // 1.5x for destinations inside nebulae
    private int transportMaxTurns = 5;

    public GovernorOptions2() {
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

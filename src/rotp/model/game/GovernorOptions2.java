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

    private int minimumMissileBases = 0;
    private boolean autospend = false;
    private int reserve = 1000;
    private boolean shipbuilding = false;
    
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

    public int getMinimumMissileBases() {
        return minimumMissileBases;
    }

    public void setMinimumMissileBases(int minimumMissileBases) {
        this.minimumMissileBases = minimumMissileBases;
    }

    public boolean isAutospend() {
        return autospend;
    }

    public void setAutospend(boolean autospend) {
        this.autospend = autospend;
    }

    public int getReserve() {
        return reserve;
    }

    public void setReserve(int reserve) {
        this.reserve = reserve;
    }

    public boolean isShipbuilding() {
        return shipbuilding;
    }

    public void setShipbuilding(boolean shipbuilding) {
        this.shipbuilding = shipbuilding;
    }
    
    
}

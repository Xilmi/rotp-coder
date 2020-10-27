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

    // if true, new colonies will have auto ship building set to "on"
    // TODO: for future use
    private boolean autoShipsByDefault = true;
    // if true, automatically scout new planets
    private boolean autoScout = true;
    // if true, automatically colonize new planets
    private boolean autoColonize = true;
    // Scout name prefix
    @Deprecated
    private String scoutPrefix = "Scout";
    // Colony ship name prefix
    @Deprecated
    private String colonyPrefix = "Col";

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

    public boolean isAutoShipsByDefault() {
        return autoShipsByDefault;
    }

    public void setAutoShipsByDefault(boolean autoShipsByDefault) {
        this.autoShipsByDefault = autoShipsByDefault;
    }

    public boolean isAutoScout() {
        return autoScout;
    }

    public void setAutoScout(boolean autoScout) {
        this.autoScout = autoScout;
    }

    public boolean isAutoColonize() {
        return autoColonize;
    }

    public void setAutoColonize(boolean autoColonize) {
        this.autoColonize = autoColonize;
    }
}

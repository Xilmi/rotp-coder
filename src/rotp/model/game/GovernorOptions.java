package rotp.model.game;

import java.io.Serializable;
import rotp.ui.UserPreferences;

/**
 * Governor options.
 */
public class GovernorOptions implements Serializable {
    private static final long serialVersionUID = 1l;

    public enum GatesGovernor {
        None,
        Rich,
        All
    }
    // keep backwards compatibility with system properties
    private boolean governorOnByDefault = UserPreferences.governorOnByDefault();
    private boolean autotransport = "true".equalsIgnoreCase(System.getProperty("autotransport", "false"));
    private GatesGovernor gates = "false".equalsIgnoreCase(System.getProperty("autogate", "true")) ? GatesGovernor.None : GatesGovernor.Rich;

    @Deprecated
    private int transportPopulation = 10;
    @Deprecated
    private int transportMaxPercent = 10;
    // 1.5x for destinations inside nebulae
    private int transportMaxTurns = 5;
    private boolean transportRichDisabled = true;
    private boolean transportPoorDouble = true;

    private int minimumMissileBases = 0;
    private boolean autospend = UserPreferences.governorAutoSpendByDefault();
    private int reserve = 1000;
    private boolean shipbuilding = true;

    // if true, new colonies will have auto ship building set to "on"
    // TODO: for future use
    private boolean autoShipsByDefault = true;
    // if true, automatically scout new planets
    private boolean autoScout = true;
    // if true, automatically colonize new planets
    private boolean autoColonize = true;
    // if true, send ships to enemy colonies
    private boolean autoAttack = false;
    // How many ships should Auto* missions send?
    private int autoScoutShipCount = 1;
    private int autoColonyShipCount = 1;
    private int autoAttackShipCount = 1;

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
        UserPreferences.setGovernorOn(governorOnByDefault);
    }

    public void setAutotransport(boolean autotransport) {
        this.autotransport = autotransport;
    }

    public void setGates(GatesGovernor gates) {
        this.gates = gates;
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
        UserPreferences.setAutoSpendOn(autospend);
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

    public boolean isTransportRichDisabled() {
        return transportRichDisabled;
    }

    public void setTransportRichDisabled(boolean transportRichDisabled) {
        this.transportRichDisabled = transportRichDisabled;
    }

    public boolean isTransportPoorDouble() {
        return transportPoorDouble;
    }

    public void setTransportPoorDouble(boolean transportPoorDouble) {
        this.transportPoorDouble = transportPoorDouble;
    }

    public boolean isAutoAttack() {
        return autoAttack;
    }

    public void setAutoAttack(boolean autoAttack) {
        this.autoAttack = autoAttack;
    }

    public int getAutoScoutShipCount() {
        return autoScoutShipCount;
    }

    public void setAutoScoutShipCount(int autoScoutShipCount) {
        this.autoScoutShipCount = autoScoutShipCount;
    }

    public int getAutoColonyShipCount() {
        return autoColonyShipCount;
    }

    public void setAutoColonyShipCount(int autoColonyShipCount) {
        this.autoColonyShipCount = autoColonyShipCount;
    }

    public int getAutoAttackShipCount() {
        return autoAttackShipCount;
    }

    public void setAutoAttackShipCount(int autoAttackShipCount) {
        this.autoAttackShipCount = autoAttackShipCount;
    }
}

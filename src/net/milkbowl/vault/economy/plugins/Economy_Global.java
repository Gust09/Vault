/* This file is part of Vault.

Vault is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Vault is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Vault.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.milkbowl.vault.economy.plugins;


import net.milkbowl.vault.economy.AbstractEconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.plussycraft.global.Global;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Economy_Global extends AbstractEconomy {
    private static final Logger log = Logger.getLogger("Minecraft");

    private final String name = "Global";
    private Plugin plugin = null;
    private Global economy = null;

    public Economy_Global(Plugin plugin){
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(new Economy_Global.EconomyServerListener(this), plugin);

        if (economy == null) {
            Plugin global = plugin.getServer().getPluginManager().getPlugin(name);

            if (global != null && global.isEnabled()) {
                economy = (Global) global;
                log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), name));
            }
        }
    }

    public class EconomyServerListener implements Listener {
        Economy_Global economy = null;

        public EconomyServerListener(Economy_Global economy) {
            this.economy = economy;
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginEnable(PluginEnableEvent event) {
            if (economy.economy == null) {
                Plugin global = event.getPlugin();

                if (global.getDescription().getName().equals(economy.name)) {
                    economy.economy = (Global) global;
                    log.info(String.format("[%s][Economy] %s hooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR)
        public void onPluginDisable(PluginDisableEvent event) {
            if (economy.economy != null) {
                if (event.getPlugin().getDescription().getName().equals(economy.name)) {
                    economy.economy = null;
                    log.info(String.format("[%s][Economy] %s unhooked.", plugin.getDescription().getName(), economy.name));
                }
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return economy != null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        int iamount = (int) Math.floor(amount);
        if (iamount == 1) {
            return String.format("%d %s", iamount, currencyNameSingular());
        } else {
            return String.format("%d %s", iamount, currencyNamePlural());
        }
    }

    @Override
    public String currencyNamePlural() {
        return "Coins";
    }

    @Override
    public String currencyNameSingular() {
        return "Coin";
    }

    @Override
    public boolean hasAccount(String playerName) {
        return economy.getPlayerManager().get(Bukkit.getOfflinePlayer(playerName)) != null;
    }

    @Override
    public double getBalance(String playerName) {
        return economy.getPlayerManager().getCached(playerName).getCoins();
    }

    @Override
    public boolean has(String playerName, double amount) {
        return getBalance(playerName) >= amount;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        EconomyResponse.ResponseType rt;
        String message;
        int iamount = (int) Math.floor(amount);

        if (has(playerName, iamount)) {
            if (economy.getPlayerManager().getCached(playerName).removeCoins(iamount, "VAULT WITHDRAW")) {
                rt = EconomyResponse.ResponseType.SUCCESS;
                message = null;
            } else {
                rt = EconomyResponse.ResponseType.SUCCESS;
                message = "ERROR";
            }
        } else {
            rt = EconomyResponse.ResponseType.FAILURE;
            message = "Not enough money";
        }

        return new EconomyResponse(iamount, getBalance(playerName), rt, message);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        EconomyResponse.ResponseType rt;
        String message;
        int iamount = (int) Math.floor(amount);

        if (economy.getPlayerManager().getCached(playerName).addCoins(iamount, "VAULT DEPOSIT")) {
            rt = EconomyResponse.ResponseType.SUCCESS;
            message = null;
        } else {
            rt = EconomyResponse.ResponseType.SUCCESS;
            message = "ERROR";
        }

        return new EconomyResponse(iamount, getBalance(playerName), rt, message);
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return new EconomyResponse(0, 0, EconomyResponse.ResponseType.NOT_IMPLEMENTED, "Global does not support bank accounts");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return false;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return false;
    }
}

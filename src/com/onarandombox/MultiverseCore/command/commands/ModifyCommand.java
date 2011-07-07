package com.onarandombox.MultiverseCore.command.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.command.BaseCommand;

enum Action {
    Set, Add, Remove, Clear
}

// This will contain all the properties that support the ADD/REMOVE
// Anything not in here will only support the SET action
enum AddProperties {
    animallist, monsterlist, blockblacklist, playerwhitelist, playerblacklist, editwhitelist, editblacklist, worldblacklist, animals, monsters
}

enum SetProperties {
    alias, animals, monsters, pvp, scaling
}

public class ModifyCommand extends BaseCommand {
    
    public ModifyCommand(MultiverseCore plugin) {
        super(plugin);
        this.name = "Modify a World";
        this.description = "Modify various aspects of worlds. See the help wiki for how to use this command properly. If you do not include a world, the current world will be used";
        this.usage = "/mvmodify" + ChatColor.GOLD + " [WORLD] " + ChatColor.GREEN + "{SET|ADD|REMOVE|CLEAR} {PROPERTY} {VALUE|all}";
        this.minArgs = 2;
        this.maxArgs = 4;
        this.identifiers.add("mvmodify");
        this.permission = "multiverse.world.modify";
        this.requiresOp = true;
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        // We NEED a world from the command line
        if (args.length == 3 && !(sender instanceof Player)) {
            sender.sendMessage("From the command line, WORLD is required.");
            sender.sendMessage("Nothing changed.");
            return;
        }
        
        MVWorld world;
        Action action;
        String value;
        String property;
        Player p;
        // Handle special CLEAR case
        if (args.length == 3) {
            p = (Player) sender;
            world = this.plugin.getMVWorld(p.getWorld().getName());
            action = getActionEnum(args[0]);
            value = args[2];
            property = args[1];
        } else {
            world = this.plugin.getMVWorld(args[0]);
            action = getActionEnum(args[1]);
            value = args[3];
            property = args[2];
        }
        
        if (world == null) {
            sender.sendMessage("That world does not exist!");
            return;
        }
        
        if (action == null) {
            sender.sendMessage("That wasn't a valid action. Valid actions are:");
            sender.sendMessage("SET, ADD or REMOVE");
            return;
        }
        
        if (!this.validateAction(action, property)) {
            sender.sendMessage("Sorry, you can't use " + action + " with " + property);
            sender.sendMessage("Please visit our wiki for more information: URLGOESHERE FERNFERRET DON'T FORGET IT!");
            return;
        }
        if (action == Action.Set) {
            if (world.setVariable(property, value)) {
                sender.sendMessage("Property " + property + " was set to " + value);
            } else {
                sender.sendMessage("There was an error setting " + property);
            }
        } else if (action == Action.Add) {
            if (world.addToList(property, value)) {
                sender.sendMessage(value + " was added to " + property);
            } else {
                sender.sendMessage(value + " could not be added to " + property);
            }
        } else if (action == Action.Remove) {
            if (world.removeFromList(property, value)) {
                sender.sendMessage(value + " was removed from " + property);
            } else {
                sender.sendMessage(value + " could not be removed from " + property);
            }
        } else if (action == Action.Clear) {
            if (world.clearList(property)) {
                sender.sendMessage(property + " was cleared. It contains 0 values now.");
            } else {
                sender.sendMessage(property + " was NOT cleared.");
            }
        }
    }
    
    private boolean validateAction(Action action, String property) {
        if (action == Action.Set) {
            try {
                SetProperties.valueOf(property);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            try {
                AddProperties.valueOf(property);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        
    }
    
    private Action getActionEnum(String action) {
        if (action.equalsIgnoreCase("set")) {
            return Action.Set;
        }
        if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("+")) {
            return Action.Add;
        }
        if (action.equalsIgnoreCase("remove") || action.equalsIgnoreCase("-")) {
            return Action.Remove;
        }
        if (action.equalsIgnoreCase("clear")) {
            return Action.Clear;
        }
        return null;
    }
    
}
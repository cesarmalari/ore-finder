package com.cesarmalari.orefinder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class FindOreCommand implements ICommand {
	private List _aliases;
	public FindOreCommand() {
		_aliases = new ArrayList();
		_aliases.add("findore");
	}

	@Override
	public int compareTo(Object arg0) {
		return 0;
	}

	@Override
	public String getCommandName() {
		return "findore";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return null;
	}

	@Override
	public List getCommandAliases() {
		return _aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		World world = sender.getEntityWorld();
		ChunkCoordinates startLocation = sender.getPlayerCoordinates();
		
		Chunk chunk = world.getChunkFromBlockCoords(startLocation.posX, startLocation.posZ);
		HashMap<String,Integer>[] data = CollectData(chunk);
		for(int y = 0; y < 256; y++) {
			for(Entry<String, Integer> item : data[y].entrySet()){
				System.out.printf("%d: %s: %d\n", y, item.getKey(), item.getValue());
			}
		}
	}
	
	private void Merge(HashMap<String, Integer>[] output, HashMap<String, Integer>[] input) throws Exception {
		if(output.length != input.length) {
			throw new Exception("Array lengths do not match");
		}
		for(int y = 0; y < 256; y++) {
			for(Entry<String, Integer> item : input[y].entrySet()){
				output[y].merge(item.getKey(), item.getValue(), (v1, v2) -> v1 + v2);
			}
		}
	}
	
	private HashMap<String, Integer>[] CollectData(Chunk chunk) {
		HashMap<String, Integer>[] data = (HashMap<String, Integer>[])new HashMap[256];
		for(int y = 0; y < 256; y++) {
			data[y] = new HashMap<String, Integer>();
		}
		for(int x=0; x < 16; x++) {
			for(int z=0; z < 16; z++) {
				for(int y = 0; y < 256; y++) {
					Block block = chunk.getBlock(x, y, z);
					if(block.getMaterial() == Material.air) {
						continue;
					}
					String name = block.getUnlocalizedName();
					data[y].merge(name, 1, (oldValue, one) -> oldValue + one);
				}
			}
		}
		return data;
	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender sender) {
		return sender instanceof EntityPlayer;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int ix) {
		return false;
	}

}

package com.cesarmalari.orefinder;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import cpw.mods.fml.relauncher.FMLInjectionData;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
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
		
		int range = 10;
		if(args.length > 0) {
			range = Integer.parseInt(args[0]);
		}
		
		try{
			File outFile = new File(getMinecraftDirectory().getAbsolutePath() + File.separator + "ores-" + UUID.randomUUID().toString() + ".json");
			if(!outFile.createNewFile()) {
				sendAndPrint(sender, String.format("Cannot create output file: %s", outFile.getAbsolutePath()));
				return;
			}

			HashMap<String, Integer>[] data = CreateData();
			int numChunks = (range * 2 + 1) * (range * 2 + 1);
			int count = 0;
			sendAndPrint(sender, String.format("Scanning %d chunks at range %d", numChunks, range));
			for(int x = chunk.xPosition - range; x <= chunk.xPosition + range; x++) {
				for(int z = chunk.zPosition - range; z <= chunk.zPosition + range; z++) {
					Chunk c = world.getChunkFromChunkCoords(x, z);
					HashMap<String,Integer>[] d = CollectData(c);
					Merge(data, d);
					count++;
					if(count % 250 == 0) {
						sendAndPrint(sender, String.format("Scanned %d chunks of %d", count, numChunks));
					}
				}
			}
			
			JsonObject output = new JsonObject();
			for(int y = 0; y < 256; y++) {
				for(Entry<String, Integer> item : data[y].entrySet()){
					JsonObject prop = (JsonObject)output.get(item.getKey());
					if(null == prop) {
						prop = new JsonObject();
						output.add(item.getKey(), prop);
					}
					prop.add(new Integer(y).toString(), new JsonPrimitive(item.getValue()));
				}
			}
			
			try(PrintWriter writer = new PrintWriter(outFile)) {
				writer.write(output.toString());
			}
			sendAndPrint(sender, String.format("JSON written to: %s", outFile.getAbsolutePath()));
		}
		catch(Exception ex) {
			sendAndPrint(sender, ex.toString());
		}
	}
	
	private void sendAndPrint(ICommandSender sender, String message) {
		sender.addChatMessage(new ChatComponentText(message));
		System.out.println(message);
	}
	
	private File getMinecraftDirectory() {
		return (File)FMLInjectionData.data()[6];
	}
	
	private void Merge(HashMap<String, Integer>[] output, HashMap<String, Integer>[] input) throws Exception {
		if(output.length != input.length) {
			throw new Exception("Array lengths do not match");
		}
		for(int y = 0; y < 256; y++) {
			for(Entry<String, Integer> item : input[y].entrySet()){
				Integer oldValue = output[y].get(item.getKey());
				output[y].put(item.getKey(), oldValue == null ? item.getValue() : oldValue + item.getValue());
			}
		}
	}
	
	private HashMap<String, Integer>[] CollectData(Chunk chunk) {
		HashMap<String, Integer>[] data = CreateData();
		for(int x=0; x < 16; x++) {
			for(int z=0; z < 16; z++) {
				for(int y = 0; y < 256; y++) {
					Block block = chunk.getBlock(x, y, z);
					if(block.getMaterial() == Material.air) {
						continue;
					}
					String name = block.getUnlocalizedName();
					Integer oldValue = data[y].get(name);
					data[y].put(name, oldValue == null ? 1 : oldValue + 1);
				}
			}
		}
		return data;
	}
	
	private HashMap<String, Integer>[] CreateData() {
		HashMap<String, Integer>[] data = (HashMap<String, Integer>[])new HashMap[256];
		for(int y = 0; y < 256; y++) {
			data[y] = new HashMap<String, Integer>();
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

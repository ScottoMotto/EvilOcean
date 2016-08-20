package net.flawedlogic.EvilOcean;

import net.flawedlogic.EvilOcean.generators.IPlatformGenerator;
import net.flawedlogic.EvilOcean.generators.RaftPlatform;
import net.flawedlogic.EvilOcean.providers.WorldProviderSurfaceOcean;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

@Mod(modid = EvilOcean.MODID, version = EvilOcean.VERSION)
public class EvilOcean
{
	@Instance("EvilOcean")
	public static EvilOcean instance;
    public static final String MODID = "EvilOcean";
    public static final String VERSION = "1.0.0";
    
    private Map<String, IPlatformGenerator> generators = Maps.newHashMap();
    
    @EventHandler
    public void preinit(FMLPreInitializationEvent event) 
    {
    	Configuration config = null;
    	
    	File cfgFile = event.getSuggestedConfigurationFile();
    	try {
    		config = new Configuration(cfgFile);
    	} catch(Exception e) {
    		FMLLog.severe("[EvilOcean] Error loading config, deleting file and resetting");
    		e.printStackTrace();
    		
    		if(cfgFile.exists()) {
    			cfgFile.delete();
    		}
    		config = new Configuration(cfgFile);
    	}
    	
    	if(config.hasChanged()) {
    		config.save();
    	}
    	
    	generators.put("raft", new RaftPlatform());
    	
    	MinecraftForge.EVENT_BUS.register(this);
    }
    
    @EventHandler
    public void load(FMLInitializationEvent event)
    {
    	FMLLog.log(Level.INFO, "[EvilOcean] initialized");
    	Hashtable<Integer, Class<? extends WorldProvider>> providers = ReflectionHelper.getPrivateValue(DimensionManager.class, null, "providers");
    	providers.put(0,  WorldProviderSurfaceOcean.class);
    }
    
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) 
    {
    	//FMLLog.log(Level.INFO, "onWorldLoad event");
        if (!event.world.isRemote && event.world instanceof WorldServer)
        {
            WorldServer world = (WorldServer)event.world;
            int spawnX = (int)(event.world.getWorldInfo().getSpawnX() / world.provider.getMovementFactor() / 16);
            int spawnZ = (int)(event.world.getWorldInfo().getSpawnZ() / world.provider.getMovementFactor() / 16);
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    world.theChunkProviderServer.loadChunk(spawnX + x, spawnZ + z);
                }
            }
        }
    }
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    }
    
    public IPlatformGenerator getPlatformType(World world)
    {
    	IPlatformGenerator ret = generators.get("raft");
    	return ret;
    }    
    
    public boolean shouldBeOcean(World world)
    {
    	// Check and see if we have a void record for the dimension in question
    	//if(dimensionIsVoid.containsKey(world.provider.dimensionId)) {
    	//	return dimensionIsVoid.get(world.provider.dimensionId);
    	//} else {
    		// If we have no record, return false (our default for now)
    	//	return false;
    	//}
    	return true;
    }
}
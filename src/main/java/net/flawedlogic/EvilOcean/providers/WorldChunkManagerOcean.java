package net.flawedlogic.EvilOcean.providers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.FMLLog;
import net.flawedlogic.EvilOcean.EvilOcean;
import net.flawedlogic.EvilOcean.biomes.BiomesOcean;
import net.flawedlogic.EvilOcean.layers.GenLayerOcean;
import net.flawedlogic.EvilOcean.generators.IPlatformGenerator;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeCache;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.WorldTypeEvent;

public class WorldChunkManagerOcean extends WorldChunkManager {
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList<BiomeGenBase> allowedBiomes = new ArrayList(Arrays.asList(new BiomeGenBase[] { BiomesOcean.desertIslands, BiomesOcean.forestIslands, BiomesOcean.mountainIslands, BiomesOcean.jungleIslands, BiomesOcean.taigaIslands }));
	private GenLayer genBiomes;
	private GenLayer biomeIndexLayer;
	private BiomeCache biomeCache;
	@SuppressWarnings("rawtypes")
	private List biomesToSpawnIn;
	private World world;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected WorldChunkManagerOcean() {
		this.biomeCache = new BiomeCache(this);
		this.biomesToSpawnIn = new ArrayList();
		this.biomesToSpawnIn.addAll(allowedBiomes);
	}

	public WorldChunkManagerOcean(long par1, WorldType par3WorldType, World world) {
		GenLayer[] agenlayer = GenLayerOcean.initializeAllBiomeGenerators(par1, par3WorldType);
		agenlayer = getModdedBiomeGenerators(par3WorldType, par1, agenlayer);
		this.genBiomes = agenlayer[0];
		this.biomeIndexLayer = agenlayer[1];
		this.world = world;
	}

	public WorldChunkManagerOcean(World par1World) {
		this(par1World.getSeed(), par1World.getWorldInfo().getTerrainType(), par1World);
	}

	@Override
	public float[] getRainfall(float[] par1ArrayOfFloat, int par2, int par3, int par4, int par5) {
		IntCache.resetIntCache();

		if ((par1ArrayOfFloat == null) || (par1ArrayOfFloat.length < par4 * par5)) {
			par1ArrayOfFloat = new float[par4 * par5];
		}

		int[] aint = this.biomeIndexLayer.getInts(par2, par3, par4, par5);

		for (int i1 = 0; i1 < par4 * par5; ++i1) {
			try {
				float f = BiomesOcean.getBiome(aint[i1]).getIntRainfall() / 65536.0F;

				if (f > 1.0F) {
					f = 1.0F;
				}

				par1ArrayOfFloat[i1] = f;
			} catch (Throwable throwable) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("DownfallBlock");
				crashreportcategory.addCrashSection("biome id", Integer.valueOf(i1));
				crashreportcategory.addCrashSection("downfalls[] size", Integer.valueOf(par1ArrayOfFloat.length));
				crashreportcategory.addCrashSection("x", Integer.valueOf(par2));
				crashreportcategory.addCrashSection("z", Integer.valueOf(par3));
				crashreportcategory.addCrashSection("w", Integer.valueOf(par4));
				crashreportcategory.addCrashSection("h", Integer.valueOf(par5));
				throw new ReportedException(crashreport);
			}
		}

		return par1ArrayOfFloat;
	}

	@Override
	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5) {
		IntCache.resetIntCache();

		if ((par1ArrayOfBiomeGenBase == null) || (par1ArrayOfBiomeGenBase.length < par4 * par5)) {
			par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
		}

		int[] aint = this.genBiomes.getInts(par2, par3, par4, par5);
		try {
			for (int i1 = 0; i1 < par4 * par5; ++i1) {
				par1ArrayOfBiomeGenBase[i1] = BiomesOcean.getBiome(aint[i1]);
			}

			return par1ArrayOfBiomeGenBase;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("RawBiomeBlock");
			crashreportcategory.addCrashSection("biomes[] size", Integer.valueOf(par1ArrayOfBiomeGenBase.length));
			crashreportcategory.addCrashSection("x", Integer.valueOf(par2));
			crashreportcategory.addCrashSection("z", Integer.valueOf(par3));
			crashreportcategory.addCrashSection("w", Integer.valueOf(par4));
			crashreportcategory.addCrashSection("h", Integer.valueOf(par5));
			throw new ReportedException(crashreport);
		}
	}

	@Override
	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] par1ArrayOfBiomeGenBase, int par2, int par3, int par4, int par5, boolean par6) {
		IntCache.resetIntCache();

		if ((par1ArrayOfBiomeGenBase == null) || (par1ArrayOfBiomeGenBase.length < par4 * par5)) {
			par1ArrayOfBiomeGenBase = new BiomeGenBase[par4 * par5];
		}

		if ((par6) && (par4 == 16) && (par5 == 16) && ((par2 & 0xF) == 0) && ((par3 & 0xF) == 0)) {
			BiomeGenBase[] abiomegenbase1 = this.biomeCache.getCachedBiomes(par2, par3);
			System.arraycopy(abiomegenbase1, 0, par1ArrayOfBiomeGenBase, 0, par4 * par5);
			return par1ArrayOfBiomeGenBase;
		}

		int[] aint = this.biomeIndexLayer.getInts(par2, par3, par4, par5);

		for (int i1 = 0; i1 < par4 * par5; ++i1) {
			par1ArrayOfBiomeGenBase[i1] = BiomesOcean.getBiome(aint[i1]);
		}

		return par1ArrayOfBiomeGenBase;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean areBiomesViable(int par1, int par2, int par3, List par4List) {
		IntCache.resetIntCache();
		int l = par1 - par3 >> 2;
		int i1 = par2 - par3 >> 2;
		int j1 = par1 + par3 >> 2;
		int k1 = par2 + par3 >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = this.genBiomes.getInts(l, i1, l1, i2);
		try {
			for (int j2 = 0; j2 < l1 * i2; ++j2) {
				BiomeGenBase biomegenbase = BiomesOcean.getBiome(aint[j2]);

				if (!(par4List.contains(biomegenbase))) {
					return false;
				}
			}

			return true;
		} catch (Throwable throwable) {
			CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Invalid Biome id");
			CrashReportCategory crashreportcategory = crashreport.makeCategory("Layer");
			crashreportcategory.addCrashSection("Layer", this.genBiomes.toString());
			crashreportcategory.addCrashSection("x", Integer.valueOf(par1));
			crashreportcategory.addCrashSection("z", Integer.valueOf(par2));
			crashreportcategory.addCrashSection("radius", Integer.valueOf(par3));
			crashreportcategory.addCrashSection("allowed", par4List);
			throw new ReportedException(crashreport);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public ChunkPosition findBiomePosition(int p_150795_1_, int p_150795_2_, int p_150795_3_, List p_150795_4_, Random p_150795_5_) {
		IntCache.resetIntCache();
		int l = p_150795_1_ - p_150795_3_ >> 2;
		int i1 = p_150795_2_ - p_150795_3_ >> 2;
		int j1 = p_150795_1_ + p_150795_3_ >> 2;
		int k1 = p_150795_2_ + p_150795_3_ >> 2;
		int l1 = j1 - l + 1;
		int i2 = k1 - i1 + 1;
		int[] aint = this.genBiomes.getInts(l, i1, l1, i2);
		ChunkPosition chunkposition = null;
		int j2 = 0;

		for (int k2 = 0; k2 < l1 * i2; ++k2) {
			int l2 = l + k2 % l1 << 2;
			int i3 = i1 + k2 / l1 << 2;
			BiomeGenBase biomegenbase = BiomesOcean.getBiome(aint[k2]);

			if ((!(p_150795_4_.contains(biomegenbase))) || ((chunkposition != null) && (p_150795_5_.nextInt(j2 + 1) != 0))) {
				continue;
			}
			chunkposition = new ChunkPosition(l2, 0, i3);
			++j2;
		}
		
        if (p_150795_1_ == 0 && p_150795_2_ == 0 && !world.getWorldInfo().isInitialized()) {
            if (chunkposition == null)
            {
            	chunkposition = new ChunkPosition(0, 0, 0);
            }

            buildSpawn(world, chunkposition.chunkPosX, world.provider.getAverageGroundLevel(), chunkposition.chunkPosZ);
        }

		return chunkposition;
	}

	@Override
	public void cleanupCache() {
		super.cleanupCache();
	}

	@Override
	public GenLayer[] getModdedBiomeGenerators(WorldType worldType, long seed, GenLayer[] original) {
		WorldTypeEvent.InitBiomeGens event = new WorldTypeEvent.InitBiomeGens(worldType, seed, original);
		MinecraftForge.TERRAIN_GEN_BUS.post(event);
		return event.newBiomeGens;
	}
	
    private void buildSpawn(World world, int x, int y, int z)
    {
        FMLLog.info("[EvilOcean] Building spawn platform at: %d, %d, %d", x, y, z);
        IPlatformGenerator platform = EvilOcean.instance.getPlatformType(world);
        platform.generate(world, x, y, z);
    }	
	
}
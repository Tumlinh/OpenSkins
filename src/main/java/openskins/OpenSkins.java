package openskins;

import java.io.File;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import openskins.asm.OSFMLLoadingPlugin;


@Mod(modid = OpenSkins.MODID, name = OpenSkins.NAME, version = OpenSkins.VERSION, acceptedMinecraftVersions = "[1.12,)", acceptableRemoteVersions = "*")

public class OpenSkins
{
	public static final String MODID = "openskins";
    public static final String NAME = "OpenSkins";
    public static final String VERSION = "2.2";
	
    // Settings
    //public static String settingsPath = "openskins.cfg";
 	public static String skinUrl1 = "http://127.0.0.1/textures.php?name=%s&skin";
 	public static String capeUrl1 = "http://127.0.0.1/textures.php?name=%s&cape";
 	public static String elytraUrl1 = "http://127.0.0.1/textures.php?name=%s&elytra";
 	public static String skinUrl2 = "http://skins.minecraft.net/MinecraftSkins/%s.png";
 	public static String capeUrl2 = "";
 	public static String elytraUrl2 = "";
 	public static boolean enableTexturesCache = true;
 	//public static int verbosity = 1;
 	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
	{
		// Load settings
		loadSettings(event.getSuggestedConfigurationFile());
	}
	
	@EventHandler
    public void init(FMLInitializationEvent event)
	{
		// Check patch success
		if (!(OSFMLLoadingPlugin.patchedClasses.isEmpty() || (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER && OSFMLLoadingPlugin.patchedClasses.size() <= OSFMLLoadingPlugin.totalClassesNbr - OSFMLLoadingPlugin.serverClassesNbr)))
			System.out.println("Patch is incomplete: OpenSkins may not work correctly");
		
		// TODO: Add command to change skin
		//ClientCommandHandler.instance.registerCommand(new CommandChangeSkin());
	}
	
	private static void loadSettings(File configFile)
	{
		// Load settings from file
		Configuration config = new Configuration(configFile);
		config.load();
		
		// Client side options
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			skinUrl1 = config.get("sources", "skinUrl1", skinUrl1, "Primary URL for skins (%s refers to the player name)").getString();
			capeUrl1 = config.get("sources", "capeUrl1", capeUrl1, "Primary URL for capes (%s refers to the player name)").getString();
			elytraUrl1 = config.get("sources", "elytraUrl1", elytraUrl1, "Primary URL for elytras (%s refers to the player name)").getString();
			skinUrl2 = config.get("sources", "skinUrl2", skinUrl2, "Secondary URL for skins (%s refers to the player name)").getString();
			capeUrl2 = config.get("sources", "capeUrl2", capeUrl2, "Secondary URL for capes (%s refers to the player name)").getString();
			elytraUrl2 = config.get("sources", "elytraUrl2", elytraUrl2, "Secondary URL for elytras (%s refers to the player name)").getString();
			enableTexturesCache = config.get("cache", "enableTexturesCache", enableTexturesCache, "Enable local cache for textures").getBoolean();
			//verbosity = settings.get(Configuration.CATEGORY_GENERAL, "verbosity", verbosity, "Verbosity level (from 0 to 2)").getInt(1);
			//verbosity = verbosity < 0 ? 0 : (verbosity > 2 ? 2 : verbosity);

			// TODO: Add more options
		}
		
		// Server side options
		else {
			// TODO: Add more options
			
			// URL to update skin server DB
			// Password to authenticate OR certificate (HTTPS feature)
		}
		
		// Save configuration
		config.save();
	}
}
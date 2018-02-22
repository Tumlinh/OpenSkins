package openskins.patch;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import openskins.OpenSkins;

public class SkinManager
{
	private static final ExecutorService THREAD_POOL = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>());
	private final static TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
	private final static String skinCacheDir = "assets/skins";
	private static MinecraftProfileTexture profileTextureBuffer;
	
	public static ResourceLocation loadSkin(final MinecraftProfileTexture profileTexture, final Type textureType, @Nullable final SkinAvailableCallback skinAvailableCallback)
    {
		profileTextureBuffer = profileTexture;
    	String hash = Integer.toHexString(profileTextureBuffer.getUrl().hashCode());
    	final ResourceLocation resourcelocation = new ResourceLocation("skins/" + hash);
        ITextureObject itextureobject = textureManager.getTexture(resourcelocation);

        if (itextureobject != null)
        {
            if (skinAvailableCallback != null)
            {
                skinAvailableCallback.skinAvailable(textureType, resourcelocation, profileTextureBuffer);
            }
        }
        else
        {
            //File file1 = new File(this.skinCacheDir, hash.length() > 2 ? hash.substring(0, 2) : "xx");
            //File file2 = new File(file1, hash);
        	File file1 = null;
        	
        	// Check whether cache is disabled
        	if (OpenSkins.enableTexturesCache)
        		file1 = new File(skinCacheDir, hash);
        	
            final IImageBuffer iimagebuffer = textureType == Type.SKIN ? new ImageBufferDownload() : null;
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file1, profileTextureBuffer.getUrl(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer()
            {
                public BufferedImage parseUserSkin(BufferedImage image)
                {
                    if (iimagebuffer != null)
                    {
                    	if (image != null)
                    		detectSkinType(image);
                        
                        image = iimagebuffer.parseUserSkin(image);
                    }

                    return image;
                }
                public void skinAvailable()
                {
                    if (iimagebuffer != null)
                    {
                        iimagebuffer.skinAvailable();
                    }

                    if (skinAvailableCallback != null)
                    {
                        skinAvailableCallback.skinAvailable(textureType, resourcelocation, profileTextureBuffer);
                    }
                }
            });
            textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
        }

        return resourcelocation;
    }

    public static void loadProfileTextures(final GameProfile profile, final net.minecraft.client.resources.SkinManager.SkinAvailableCallback skinAvailableCallback, final boolean requireSecure)
    {
        THREAD_POOL.submit(new Runnable()
        {
            public void run()
            {
                /*final Map<Type, MinecraftProfileTexture> map = Maps.<Type, MinecraftProfileTexture>newHashMap();

                try
                {
                    map.putAll(SkinManager.this.sessionService.getTextures(profile, requireSecure));
                }
                catch (InsecureTextureException var3)
                {
                    ;
                }

                if (map.isEmpty() && profile.getId().equals(Minecraft.getMinecraft().getSession().getProfile().getId()))
                {
                    profile.getProperties().clear();
                    profile.getProperties().putAll(Minecraft.getMinecraft().getProfileProperties());
                    map.putAll(SkinManager.this.sessionService.getTextures(profile, false));
                }*/

                Minecraft.getMinecraft().addScheduledTask(new Runnable()
                {
                    public void run()
                    {
                    	/*
                        if (map.containsKey(Type.SKIN))
                        {
                            SkinManager.this.loadSkin((MinecraftProfileTexture)map.get(Type.SKIN), Type.SKIN, skinAvailableCallback);
                    	}
                        
                        if (map.containsKey(Type.CAPE))
                        {
                            SkinManager.this.loadSkin((MinecraftProfileTexture)map.get(Type.CAPE), Type.CAPE, skinAvailableCallback);
                        }
                        */
                    	
                    	// TODO
                    	// Get textures names from the skin server
                    	// OR do it in the first 'run()'
                    	// Create a map containing the available textures for the player
                    	String skinUrl = String.format(OpenSkins.skinUrl1, StringUtils.stripControlCodes(profile.getName()));
                    	String capeUrl = String.format(OpenSkins.capeUrl1, StringUtils.stripControlCodes(profile.getName()));
                    	String elytraUrl = String.format(OpenSkins.elytraUrl1, StringUtils.stripControlCodes(profile.getName()));
                    	Map<String, String> metadata = Maps.<String, String>newHashMap();
                    	metadata.put("name", profile.getName()); // pass player name to 'loadSkin()'
                    	
                    	// TODO
                    	// Detect skin type
                    	// This should be done after downloading image and before parsing skin => just before skinAvailableCallback
                    	//metadata.put("model", "slim"); // or do nothing for the legacy model
                    	
                    	// Comment to disable a feature
                    	loadSkin(new MinecraftProfileTexture(skinUrl, metadata), Type.SKIN, skinAvailableCallback);
                    	loadSkin(new MinecraftProfileTexture(capeUrl, metadata), Type.CAPE, skinAvailableCallback);
                    	loadSkin(new MinecraftProfileTexture(elytraUrl, metadata), Type.ELYTRA, skinAvailableCallback);
                    }
                });
            }
        });
    }

    public static Map<Type, MinecraftProfileTexture> loadSkinFromCache(GameProfile profile)
    {
    	String skinUrl = String.format(OpenSkins.skinUrl1, StringUtils.stripControlCodes(profile.getName()));
    	Map<String, String> metadata = Maps.<String, String>newHashMap();
    	metadata.put("name", profile.getName()); // pass player name to 'loadSkin()'
    	Map<Type, MinecraftProfileTexture> map = Maps.<Type, MinecraftProfileTexture>newHashMap();
    	map.put(Type.SKIN, new MinecraftProfileTexture(skinUrl, metadata));
    	return map;
    }
    
    // Detect skin type (Alex or Steve)
    public static void detectSkinType(BufferedImage image)
    {
    	int xShoulder = 50 * image.getWidth() / 64;
    	int divider = image.getWidth() == image.getHeight() ? 64 : 32;
    	int yShoulder = 16 * image.getHeight() / divider;
    	int pixel = image.getRGB(xShoulder, yShoulder);
    	String skinType = new Color(pixel, true).getAlpha() == 0 ? "slim" : "default";
    	
    	// Set skin type (create a new MinecraftProfileTexture to change metadata)
    	Map<String, String> metadata = Maps.<String, String>newHashMap();
    	metadata.put("model", skinType);
    	profileTextureBuffer = new MinecraftProfileTexture(profileTextureBuffer.getUrl(), metadata);
    }
}
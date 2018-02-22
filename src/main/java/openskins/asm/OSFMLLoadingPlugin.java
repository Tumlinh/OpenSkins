package openskins.asm;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import static org.objectweb.asm.Opcodes.*;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

public class OSFMLLoadingPlugin implements IFMLLoadingPlugin
{
	public static File location;
	public static ArrayList<PatchedClass> patchedClasses;
	public static int totalClassesNbr = 3;
	public static int serverClassesNbr = 1;

	public String[] getLibraryRequestClass()
	{
		return null;
	}

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[]{OSClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		// Get MC location
		location = (File) data.get("mcLocation");

		// Prepare list of patched classes
		patchedClasses = new ArrayList<PatchedClass>();

		// TileEntitySkull
		patchedClasses.add(new PatchedClass("net.minecraft.tileentity.TileEntitySkull", "TileEntitySkull", "awb", "8c648e2785053d1ec177604af5e614a1",
				new ArrayList<PatchedMethod>() {{
					add(new PatchedMethod("updateGameprofile", "b", "(Lcom/mojang/authlib/GameProfile;)Lcom/mojang/authlib/GameProfile;",
							new InsnList() {{
								// Return input
								add(new VarInsnNode(ALOAD, 0));
								add(new InsnNode(ARETURN));
							}}
							));
				}}
				));

		// AbstractClientPlayer
		patchedClasses.add(new PatchedClass("net.minecraft.client.entity.AbstractClientPlayer", "AbstractClientPlayer", "bty", "2e6975f3f72b0c5e4a5e17ce32248c5c",
				new ArrayList<PatchedMethod>() {{
					add(new PatchedMethod("getDownloadImageSkin", "a", "(Lnf;Ljava/lang/String;)Lcdh;",
							new InsnList() {{
								// Call openskins.patch.AbstractClientPlayer.getDownloadImageSkin(...)
								add(new VarInsnNode(ALOAD, 0));
								add(new VarInsnNode(ALOAD, 1));
								add(new MethodInsnNode(INVOKESTATIC, "openskins/patch/AbstractClientPlayer", "getDownloadImageSkin", "(Lnet/minecraft/util/ResourceLocation;Ljava/lang/String;)Lnet/minecraft/client/renderer/ThreadDownloadImageData;", false));
								add(new InsnNode(ARETURN));
							}}
							));
				}}
				));

		// SkinManager
		patchedClasses.add(new PatchedClass("net.minecraft.client.resources.SkinManager", "SkinManager", "cev", "d535824ebf2513e3db5fa0e5aba2cf60",
				new ArrayList<PatchedMethod>() {{
					add(new PatchedMethod("loadSkin", "a", "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lcex$a;)Lnf;",
							new InsnList() {{
								// Call openskins.patch.SkinManager.loadSkin(...)
								add(new VarInsnNode(ALOAD, 1));
								add(new VarInsnNode(ALOAD, 2));
								add(new VarInsnNode(ALOAD, 3));
								add(new MethodInsnNode(INVOKESTATIC, "openskins/patch/SkinManager", "loadSkin", "(Lcom/mojang/authlib/minecraft/MinecraftProfileTexture;Lcom/mojang/authlib/minecraft/MinecraftProfileTexture$Type;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;)Lnet/minecraft/util/ResourceLocation;", false));
								add(new InsnNode(ARETURN));
							}}
							));
					add(new PatchedMethod("loadProfileTextures", "a", "(Lcom/mojang/authlib/GameProfile;Lcex$a;Z)V",
							new InsnList() {{
								// Call openskins.patch.SkinManager.loadProfileTextures(...)
								add(new VarInsnNode(ALOAD, 1));
								add(new VarInsnNode(ALOAD, 2));
								add(new VarInsnNode(ILOAD, 3));
								add(new MethodInsnNode(INVOKESTATIC, "openskins/patch/SkinManager", "loadProfileTextures", "(Lcom/mojang/authlib/GameProfile;Lnet/minecraft/client/resources/SkinManager$SkinAvailableCallback;Z)V", false));
								add(new InsnNode(RETURN));
							}}
							));
					add(new PatchedMethod("loadSkinFromCache", "a", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;",
							new InsnList() {{
								// Call openskins.patch.SkinManager.loadSkinFromCache(...)
								add(new VarInsnNode(ALOAD, 1));
								add(new MethodInsnNode(INVOKESTATIC, "openskins/patch/SkinManager", "loadSkinFromCache", "(Lcom/mojang/authlib/GameProfile;)Ljava/util/Map;", false));
								add(new InsnNode(ARETURN));
							}}
							));
				}}
				));
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
}